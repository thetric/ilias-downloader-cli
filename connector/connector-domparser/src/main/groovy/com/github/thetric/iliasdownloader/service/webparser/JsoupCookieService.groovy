package com.github.thetric.iliasdownloader.service.webparser

import groovy.transform.CompileStatic
import org.jsoup.Jsoup

@CompileStatic
final class JsoupCookieService implements CookieService {
    @Override
    String getCookieFromUrl(final String url, final String cookie) throws IOException {
        def response = Jsoup.connect(url).execute()
        return response.cookie(cookie)
    }
}
