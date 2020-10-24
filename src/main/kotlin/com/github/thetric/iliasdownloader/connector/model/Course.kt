package com.github.thetric.iliasdownloader.connector.model

data class Course(
    val id: Long,
    override val name: String,
    override val url: String
) : IliasItem
