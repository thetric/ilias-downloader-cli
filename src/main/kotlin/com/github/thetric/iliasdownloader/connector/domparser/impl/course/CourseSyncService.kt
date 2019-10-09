package com.github.thetric.iliasdownloader.connector.domparser.impl.course

import com.github.thetric.iliasdownloader.connector.api.ContextAwareIliasItemVisitor
import com.github.thetric.iliasdownloader.connector.api.model.Course
import com.github.thetric.iliasdownloader.connector.api.model.IliasItem

/**
 * Provides methods for access to Ilias courses.
 */
interface CourseSyncService {
    val joinedCourses: Collection<Course>

    fun <C> visit(
        parentContext: C,
        courseItem: IliasItem,
        itemVisitor: ContextAwareIliasItemVisitor<C>
    )
}
