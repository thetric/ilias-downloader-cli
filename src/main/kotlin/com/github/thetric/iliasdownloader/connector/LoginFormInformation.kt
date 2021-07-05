package com.github.thetric.iliasdownloader.connector

internal data class LoginFormInformation(
    val clientId: String,
    val loginUrl: String,
    val userNameFieldName: String,
    val passwordFieldName: String,
    val hiddenValues: Map<String, String>
)
