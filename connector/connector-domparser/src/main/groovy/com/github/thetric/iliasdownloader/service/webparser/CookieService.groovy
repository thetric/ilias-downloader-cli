package com.github.thetric.iliasdownloader.service.webparser

interface CookieService {
    String getCookieFromUrl(final String url, final String cookieName) throws IOException
}
