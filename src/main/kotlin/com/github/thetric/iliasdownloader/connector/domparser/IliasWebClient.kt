package com.github.thetric.iliasdownloader.connector.domparser

import com.github.thetric.iliasdownloader.connector.api.exception.IliasAuthenticationException
import com.github.thetric.iliasdownloader.connector.api.model.LoginCredentials
import mu.KotlinLogging
import okhttp3.FormBody
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.InputStream
import java.net.CookieManager
import java.net.CookiePolicy

internal class IliasWebClient(iliasBaseUrl: String) : AutoCloseable {
    private val client: OkHttpClient
    private val cookieManager: CookieManager = CookieManager()
    private val loginPage =
        "${iliasBaseUrl}ilias.php?lang=en&cmd=post&cmdClass=ilstartupgui&cmdNode=w9&baseClass=ilStartUpGUI&rtoken="
    private val logoutPage = "${iliasBaseUrl}logout.php"
    private val log = KotlinLogging.logger {}

    init {
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL)
        client = OkHttpClient.Builder()
            .cookieJar(JavaNetCookieJar(cookieManager))
            .followRedirects(false)
            .build()
    }

    fun login(credentials: LoginCredentials) {
        log.info { "Logging in at $loginPage" }
        val loginForm = FormBody.Builder()
            .add("username", credentials.userName)
            .add("password", credentials.password)
            // magic string to make the login work
            .add("cmd[doStandardAuthentication]", "Anmelden")
            .build()
        val request = Request.Builder()
            .url(loginPage)
            .post(loginForm)
            .build()
        val loginClient = client.newBuilder()
            .followRedirects(true)
            .build()
        loginClient.newCall(request).execute().use {
            checkResponse(loginPage, it)
            if (it.request.url.toString().startsWith(loginPage)) {
                clearCookies()
                throw IliasAuthenticationException("Login at $loginPage failed. Invalid credentials")
            }
            log.info { "Login at $loginPage succeeded " }
        }
    }

    fun logout() {
        log.info { "Logging out: $logoutPage" }
        val response = executeGetRequest(logoutPage)
        clearCookies()
        checkResponse(logoutPage, response)
        log.info { "Logout at ${logoutPage}succeeded" }
    }

    private fun clearCookies() {
        cookieManager.cookieStore.removeAll()
    }

    private fun executeGetRequest(url: String): Response {
        val request = Request.Builder().url(url).build()
        return client.newCall(request).execute()
    }

    fun getHtml(url: String): String {
        val response = executeGetRequest(url)
        checkResponse(url, response)
        return response.body!!.string()
    }

    fun getAsInputStream(url: String): InputStream {
        val response = executeGetRequest(url)
        checkResponse(url, response)
        return response.body!!.byteStream()
    }

    private fun checkResponse(url: String, response: Response) {
        if (!response.isSuccessful) {
            val msg = "Failed to GET $url: ${response.message}"
            throw IliasHttpException(msg, url, response.code)
        }
    }

    override fun close() {
        client.dispatcher.executorService.shutdown()
        client.connectionPool.evictAll()
        client.cache?.close()
    }
}
