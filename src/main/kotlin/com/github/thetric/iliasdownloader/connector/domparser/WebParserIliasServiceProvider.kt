package com.github.thetric.iliasdownloader.connector.domparser

import com.github.thetric.iliasdownloader.connector.api.IliasService
import com.github.thetric.iliasdownloader.connector.api.IliasServiceProvider
import com.github.thetric.iliasdownloader.connector.domparser.impl.CookieNotFoundException
import com.github.thetric.iliasdownloader.connector.domparser.impl.WebIliasService
import com.github.thetric.iliasdownloader.connector.domparser.impl.course.CourseSyncServiceImpl
import com.github.thetric.iliasdownloader.connector.domparser.impl.course.IliasItemParser
import com.github.thetric.iliasdownloader.connector.domparser.impl.course.IliasItemParserImpl
import com.github.thetric.iliasdownloader.connector.domparser.impl.course.jsoup.JSoupParserServiceImpl
import com.github.thetric.iliasdownloader.connector.domparser.impl.webclient.OkHttpIliasWebClient
import java.io.IOException

private const val LOGIN_PAGE_NAME = "login.php"
private const val ILIAS_CLIENT_ID_COOKIE_NAME = "ilClientId"

/**
 * Provides an [IliasService] that retrieves information from the Ilias service by parsing the HTML pages.
 */
class WebParserIliasServiceProvider @Throws(IOException::class)
constructor(
    private val cookieService: CookieService,
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
        try {
            return cookieService.getCookieFromUrl(
                loginPage,
                ILIAS_CLIENT_ID_COOKIE_NAME
            ) ?: throw CookieNotFoundException(
                "Konnte das Cookie '$ILIAS_CLIENT_ID_COOKIE_NAME\' nicht in der Response von der Seite " +
                    "$loginPage finden"
            )
        } catch (e: IOException) {
            throw IOException("Konnte die URL \'$loginPage\' nicht erreichen", e)
        }
    }

    override fun newInstance(): IliasService {
        val jSoupParserService = JSoupParserServiceImpl()
        val iliasWebClient = OkHttpIliasWebClient(iliasBaseUrl)
        val courseOverview = "${iliasBaseUrl}ilias.php?baseClass=ilrepositorygui&reloadpublic=1&cmd=frameset&ref_id=1"
        val courseLinkPrefix = "${iliasBaseUrl}goto_${clientId}_crs_"
        val courseWebDavPrefix = "${iliasBaseUrl}webdav.php/$clientId/ref_"
        val itemParser: IliasItemParser =
            IliasItemParserImpl(courseWebDavPrefix, courseLinkPrefix)
        val courseSyncServiceProvider = CourseSyncServiceImpl(
            jSoupParserService,
            iliasWebClient,
            itemParser, courseOverview
        )
        return WebIliasService(courseSyncServiceProvider, iliasWebClient)
    }
}
