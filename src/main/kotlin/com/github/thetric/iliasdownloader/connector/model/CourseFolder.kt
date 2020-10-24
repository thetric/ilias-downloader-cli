package com.github.thetric.iliasdownloader.connector.model

/**
 * Course folder in an Ilias [Course].
 */
data class CourseFolder(
    override val name: String,
    override val url: String
) : IliasItem
