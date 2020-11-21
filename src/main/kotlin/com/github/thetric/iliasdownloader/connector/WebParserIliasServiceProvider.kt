package com.github.thetric.iliasdownloader.connector

import com.github.thetric.iliasdownloader.connector.exception.CookieNotFoundException
import com.github.thetric.iliasdownloader.connector.exception.IliasException
import mu.KotlinLogging
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import java.io.IOException
import java.net.URI
import java.net.URL

private const val LOGIN_PAGE_NAME = "login.php"
private const val ILIAS_CLIENT_ID_COOKIE_NAME = "ilClientId"

/**
 * Provides an [IliasService] that retrieves information from the Ilias service by parsing the HTML pages.
 */
class WebParserIliasServiceProvider(private val loginPage: String) : IliasServiceProvider {
    private val cssSelectorIliasFormLogin = "form[name='formlogin']"
    private val cssSelectorExternalLoginProbableLoginForm = "form:not([action=''])[action]"
    private val log = KotlinLogging.logger {}

    override fun newInstance(): IliasService {
        val iliasBaseUrl = retrieveBaseUrl(loginPage)
        val iliasHost = "https://${URL(iliasBaseUrl).host}"
        val loginFormInformation = retrieveClientId(loginPage, iliasBaseUrl)

        val courseOverview = "${iliasBaseUrl}ilias.php?baseClass=ilPersonalDesktopGUI&cmd=jumpToSelectedItems"
        val courseLinkPrefix = "${iliasBaseUrl}goto_${loginFormInformation.clientId}_crs_"
        val courseWebDavPrefix = "${iliasBaseUrl}webdav.php/${loginFormInformation.clientId}/ref_"
        val itemParser = IliasItemParser(courseWebDavPrefix, courseLinkPrefix)
        val courseSyncService = CourseSyncService(iliasHost)
        return WebIliasService(itemParser, courseSyncService, iliasBaseUrl, loginFormInformation, courseOverview)
    }

    private fun retrieveBaseUrl(loginPage: String): String {
        var trimmed = loginPage.trim { it <= ' ' }
        require(trimmed.isNotEmpty()) {
            "The given login page URL must not be empty"
        }

        if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://")) {
            trimmed = "https://$loginPage"
        }

        val loginPageNameIndex = trimmed.indexOf(LOGIN_PAGE_NAME)
        require(loginPageNameIndex != -1) {
            "The given login page URL does not contain \'$LOGIN_PAGE_NAME\'"
        }

        return trimmed.substring(0, loginPageNameIndex)
    }

    private fun retrieveClientId(iliasDirectLoginPage: String, iliasBaseUrl: String): LoginFormInformation {
        return try {
            val response = Jsoup.connect(iliasDirectLoginPage).execute()
            val clientId = (response.cookie(ILIAS_CLIENT_ID_COOKIE_NAME)
                ?: throw CookieNotFoundException(
                    "Could not find the required cookie '$ILIAS_CLIENT_ID_COOKIE_NAME\' in the response "
                        + "from $iliasDirectLoginPage"
                ))
            val loginPageDocument = response.parse()
            val loginForm = loginPageDocument.selectFirst(cssSelectorIliasFormLogin)
            val loginAction = loginForm.attr("action")
            if (!loginAction.startsWith("ilias.php")) {
                getLoginFormInformationFromExternalAuthProvider(iliasBaseUrl, loginAction, clientId)
            } else {
                val iliasLoginProcessingPage =
                    "${iliasBaseUrl}ilias.php?lang=en&client_id=${clientId}&cmd=post&cmdClass=ilstartupgui&cmdNode=yc" +
                        "&baseClass=ilStartUpGUI&rtoken="
                // magic string to make the login work
                val hiddenValues = mapOf("cmd[doStandardAuthentication]" to "Login")
                LoginFormInformation(clientId, iliasLoginProcessingPage, "username", "password", hiddenValues)
            }
        } catch (e: IOException) {
            throw IOException("Konnte die URL \'$iliasDirectLoginPage\' nicht erreichen", e)
        }
    }

    private fun getLoginFormInformationFromExternalAuthProvider(
        iliasBaseUrl: String,
        loginAction: String?,
        clientId: String
    ): LoginFormInformation {
        // looks like an external login provider, follow it
        val externalLoginUrl = URL(iliasBaseUrl + loginAction).toURI().normalize()
        log.info { "External login provider detected, following $externalLoginUrl" }
        val res = Jsoup.connect(externalLoginUrl.toString()).followRedirects(true).execute()
        log.info { "External login provider is located at ${res.url()}" }
        val loginFormInputs = findLoginForm(res, externalLoginUrl)
        return retrieveLoginFormInformationFromExternalAuthProviderForm(loginFormInputs, externalLoginUrl, clientId)
    }

    private fun retrieveLoginFormInformationFromExternalAuthProviderForm(
        loginFormInputs: Elements,
        externalLoginUrl: URI,
        clientId: String
    ): LoginFormInformation {
        var usernameFieldName: String? = null
        var passwordFieldName: String? = null
        val hiddenValues = mutableMapOf<String, String>()
        for (inputElement in loginFormInputs) {
            val name = inputElement.attr("name")
            when (inputElement.attr("type")) {
                "hidden" -> {
                    hiddenValues[name] = inputElement.attr("value")
                }
                "text" -> {
                    if (usernameFieldName != null) {
                        throw IliasException("Unable to identify the user name fields in the external login provider $externalLoginUrl. Possible text input names: $usernameFieldName, name")
                    }
                    usernameFieldName = name
                }
                "password" -> {
                    if (passwordFieldName != null) {
                        throw IliasException("Unable to identify the password fields in the external login provider $externalLoginUrl. Possible password input names: $passwordFieldName, name")
                    }
                    passwordFieldName = name
                }
            }
        }
        if (usernameFieldName == null || passwordFieldName == null) {
            throw IliasException("Failed to detect the username or password input. Detected username input name: $usernameFieldName, password input name: $passwordFieldName")
        }
        return LoginFormInformation(
            clientId,
            externalLoginUrl.toString(),
            usernameFieldName,
            passwordFieldName,
            hiddenValues
        )
    }

    private fun findLoginForm(res: Connection.Response, externalLoginUrl: URI): Elements {
        val thirdPartyLoginFormDocument = res.parse()
        val probableLoginForms = thirdPartyLoginFormDocument.select(cssSelectorExternalLoginProbableLoginForm)
        log.debug { "Found ${probableLoginForms.size} probable login forms:" }
        if (log.isDebugEnabled) {
            log.debug { "Attributes of the login forms:" }
            probableLoginForms.forEach {
                log.debug { it.attributes() }
            }
        }
        if (probableLoginForms.size != 1) {
            log.warn { "Attributes of the login forms:" }
            probableLoginForms.forEach {
                log.warn { it.attributes() }
            }
            throw IliasException("Unable to identify the login forms of the external login provider $externalLoginUrl. Expected exactly 1 but got ${probableLoginForms.size}.")
        }
        return probableLoginForms[0].select("input[type][name]")
    }
}
