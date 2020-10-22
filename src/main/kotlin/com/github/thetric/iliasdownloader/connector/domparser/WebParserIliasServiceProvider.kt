package com.github.thetric.iliasdownloader.connector.domparser

import com.github.thetric.iliasdownloader.connector.api.IliasService
import com.github.thetric.iliasdownloader.connector.api.IliasServiceProvider
import org.jsoup.Jsoup
import java.io.IOException

private const val LOGIN_PAGE_NAME = "login.php"
private const val ILIAS_CLIENT_ID_COOKIE_NAME = "ilClientId"

/**
 * Provides an [IliasService] that retrieves information from the Ilias service by parsing the HTML pages.
 */
class WebParserIliasServiceProvider @Throws(IOException::class)
constructor(
    loginPage: String
) : IliasServiceProvider {
    private val iliasBaseUrl: String
    private val clientId: String

    init {
        iliasBaseUrl = retrieveBaseUrl(loginPage)
        clientId = retrieveClientId(loginPage)
    }

    private fun retrieveBaseUrl(loginPage: String): String {
        var trimmed = loginPage.trim { it <= ' ' }
        require(trimmed.isNotEmpty()) {
            "Die angegebene Loginseiten URL darf nicht leer sein"
        }

        if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://")) {
            trimmed = "https://$loginPage"
        }

        val loginPageNameIndex = trimmed.indexOf(LOGIN_PAGE_NAME)
        require(loginPageNameIndex != -1) {
            "Die angegebene URL enthält kein \'$LOGIN_PAGE_NAME\'"
        }

        return trimmed.substring(0, loginPageNameIndex)
    }

    @Throws(IOException::class)
    private fun retrieveClientId(loginPage: String): String {
        return try {
            val response = Jsoup.connect(loginPage).execute()
            response.cookie(ILIAS_CLIENT_ID_COOKIE_NAME)
                ?: throw CookieNotFoundException(
                    "Konnte das Cookie '$ILIAS_CLIENT_ID_COOKIE_NAME\' nicht in der "
                        + "Response von der Seite $loginPage finden"
                )
        } catch (e: IOException) {
            throw IOException("Konnte die URL \'$loginPage\' nicht erreichen", e)
        }
    }

    override fun newInstance(): IliasService {
        val iliasWebClient = IliasWebClient(iliasBaseUrl, clientId)
        val courseOverview = "${iliasBaseUrl}ilias.php?baseClass=ilPersonalDesktopGUI&cmd=jumpToSelectedItems"
        val courseLinkPrefix = "${iliasBaseUrl}goto_${clientId}_crs_"
        val courseWebDavPrefix = "${iliasBaseUrl}webdav.php/$clientId/ref_"
        val itemParser = IliasItemParser(courseWebDavPrefix, courseLinkPrefix)
        val courseSyncServiceProvider = CourseSyncService(iliasWebClient, itemParser, courseOverview)
        return WebIliasService(courseSyncServiceProvider, iliasWebClient)
    }
}
