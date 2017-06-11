package com.github.thetric.iliasdownloader.service.webparser

import groovy.transform.CompileStatic
import org.jsoup.Connection
import org.jsoup.Jsoup

/**
 * {@link CookieService} implementation with {@link Jsoup}.
 */
@CompileStatic
final class JsoupCookieService implements CookieService {
    @Override
    String getCookieFromUrl(final String url, final String cookie) throws IOException {
        final Connection.Response response = Jsoup.connect(url).execute()
        return response.cookie(cookie)
    }
}
