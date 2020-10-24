package com.github.thetric.iliasdownloader.connector.api.model

import java.time.LocalDateTime

/**
 * Represent a Ilias file (without the actual content, just an URL).
 */
data class CourseFile(
    override val name: String,
    override val url: String,

    val modified: LocalDateTime,
    val size: Long
) : IliasItem
