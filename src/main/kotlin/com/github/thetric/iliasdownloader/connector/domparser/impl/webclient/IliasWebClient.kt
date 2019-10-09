package com.github.thetric.iliasdownloader.connector.domparser.impl.webclient

import com.github.thetric.iliasdownloader.connector.api.model.LoginCredentials

import java.io.InputStream

/**
 *
 */
interface IliasWebClient {
    fun getAsInputStream(url: String): InputStream

    fun login(credentials: LoginCredentials)

    fun logout()

    fun getHtml(url: String): String
}
