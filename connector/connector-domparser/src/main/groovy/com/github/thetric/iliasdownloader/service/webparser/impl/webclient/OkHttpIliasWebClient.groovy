package com.github.thetric.iliasdownloader.service.webparser.impl.webclient

import com.github.thetric.iliasdownloader.service.exception.IliasAuthenticationException
import com.github.thetric.iliasdownloader.service.exception.IliasException
import com.github.thetric.iliasdownloader.service.model.LoginCredentials
import groovy.transform.CompileStatic
import groovy.util.logging.Log4j2
import okhttp3.*

/**
 * {@link IliasWebClient} communicating with the Ilias with OkHttp.
 */
@Log4j2
@CompileStatic
final class OkHttpIliasWebClient implements IliasWebClient {
    private final OkHttpClient client
    private final CookieManager cookieManager

    private final String iliasBaseUrl
    private final String clientId

    private final String loginPage
    private final String logoutPage

    OkHttpIliasWebClient(final String iliasBaseUrl, final String clientId) {
        this.iliasBaseUrl = iliasBaseUrl
        this.clientId = clientId
        loginPage = "${iliasBaseUrl}login.php"
        logoutPage = "${iliasBaseUrl}logout.php"

        cookieManager = new CookieManager()
        cookieManager.cookiePolicy = CookiePolicy.ACCEPT_ALL
        client = new OkHttpClient.Builder().cookieJar(new JavaNetCookieJar(cookieManager))
                                           .followRedirects(false)
                                           .build()
    }

    @Override
    void login(final LoginCredentials credentials) {
        log.info('Logging in at {}', loginPage)
        final loginForm = new FormBody.Builder().add('username', credentials.userName)
                                                .add('password', credentials.password)
                                                .build()
        final Request request = new Request.Builder().url(loginPage)
                                                     .post(loginForm)
                                                     .build()
        final loginClient = client.newBuilder()
                                      .followRedirects(true)
                                      .build()
        final Response response = loginClient.newCall(request)
                                             .execute()
        checkResponse(loginPage, response)
        if (response.request().url().toString().startsWith(loginPage)) {
            clearCookies()
            throw new IliasAuthenticationException("Login at $loginPage failed. Invalid credentials")
        }
        log.info('Login at {} succeeded', loginPage)
    }

    @Override
    void logout() {
        log.info('Logging out: {}', logoutPage)
        final response = executeGetRequest(logoutPage)
        // TODO check response
        clearCookies()
        checkResponse(logoutPage, response)
        log.info('Logout at {} succeeded', logoutPage)
    }

    private void clearCookies() {
        cookieManager.cookieStore.removeAll()
    }

    private Response executeGetRequest(final String url) {
        final Request request = new Request.Builder().url(url).build()
        return client.newCall(request).execute()
    }

    @Override
    String getHtml(final String url) {
        final response = executeGetRequest(url)
        checkResponse(url, response)
        return response.body().string()
    }

    @Override
    InputStream getAsInputStream(final String url) {
        final Response response = executeGetRequest(url)
        checkResponse(url, response)
        return response.body().byteStream()
    }

    private void checkResponse(final String url, final Response response) {
        if (!response.successful) {
            throw new IliasException("Failed to GET $url: ${response.message()}")
        }
    }

}
