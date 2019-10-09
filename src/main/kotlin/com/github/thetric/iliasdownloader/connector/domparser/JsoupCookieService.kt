package com.github.thetric.iliasdownloader.connector.domparser

import org.jsoup.Jsoup
import java.io.IOException

/**
 * [CookieService] implementation with [Jsoup].
 */
class JsoupCookieService : CookieService {
    @Throws(IOException::class)
    override fun getCookieFromUrl(url: String, cookieName: String): String? {
        val response = Jsoup.connect(url).execute()
        return response.cookie(cookieName)
    }

}
