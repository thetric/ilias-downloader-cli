package com.github.thetric.iliasdownloader.service.webparser

/**
 * Retrieves a cookie from a request to the specified URL.
 * The request contains <b>no authentication information</b>.
 */
interface CookieService {
    String getCookieFromUrl(final String url, final String cookieName) throws IOException
}
