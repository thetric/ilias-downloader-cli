package com.github.thetric.iliasdownloader.service.webparser.impl.course

import com.github.thetric.iliasdownloader.service.IliasService
import com.github.thetric.iliasdownloader.service.model.Course
import com.github.thetric.iliasdownloader.service.model.IliasItem

interface CourseSyncService {
    Collection<Course> getJoinedCourses()

    IliasService.VisitResult visit(IliasItem courseItem, Closure<IliasService.VisitResult> visitMethod)
}
