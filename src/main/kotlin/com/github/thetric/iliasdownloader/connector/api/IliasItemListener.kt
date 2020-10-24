package com.github.thetric.iliasdownloader.connector.api

import com.github.thetric.iliasdownloader.connector.api.model.CourseFile
import com.github.thetric.iliasdownloader.connector.api.model.CourseFolder

/**
 * Visitor for the traversal of the Ilias course items.
 *
 * @param C context of the traversal (e.g. the current dir, upload URL)
 */
interface IliasItemListener<C> {

    /**
     * Handles the visitation of a [CourseFolder].
     *
     * @return resolved context of the child items.
     */
    fun handleFolder(parentContext: C, folder: CourseFolder): C

    fun handleFile(parentContext: C, file: CourseFile)
}
