package com.github.thetric.iliasdownloader.connector.model

/**
 * Base interface for all Ilias items.
 *
 * @see Course
 *
 * @see CourseFile
 *
 * @see CourseFolder
 */
interface IliasItem {
    val name: String
    val url: String
}
