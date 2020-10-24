package com.github.thetric.iliasdownloader.connector.model

/**
 * Represents the login credentials necessary for an Ilias login.
 *
 * @see com.github.thetric.iliasdownloader.connector.IliasService
 */
data class LoginCredentials(
    val userName: String,
    val password: String
)
