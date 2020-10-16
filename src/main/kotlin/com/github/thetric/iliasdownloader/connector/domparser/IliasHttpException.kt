package com.github.thetric.iliasdownloader.connector.domparser

import com.github.thetric.iliasdownloader.connector.api.exception.IliasException

class IliasHttpException(
    override val message: String,
    val url: String,
    val statusCode: Int
) : IliasException(message)
