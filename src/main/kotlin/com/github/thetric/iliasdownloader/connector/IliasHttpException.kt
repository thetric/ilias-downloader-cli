package com.github.thetric.iliasdownloader.connector

import com.github.thetric.iliasdownloader.connector.exception.IliasException

class IliasHttpException(
    override val message: String,
    val url: String,
    val statusCode: Int
) : IliasException(message)
