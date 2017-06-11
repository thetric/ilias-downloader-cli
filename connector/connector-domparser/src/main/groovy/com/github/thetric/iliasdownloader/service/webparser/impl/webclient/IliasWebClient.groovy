package com.github.thetric.iliasdownloader.service.webparser.impl.webclient

import com.github.thetric.iliasdownloader.service.model.LoginCredentials

/**
 *
 */
interface IliasWebClient {
    InputStream getAsInputStream(final String url)

    void login(final LoginCredentials credentials)

    void logout()

    String getHtml(final String url)
}
