package com.github.thetric.iliasdownloader.service.webparser.impl.course

import com.github.thetric.iliasdownloader.service.IliasService
import com.github.thetric.iliasdownloader.service.model.Course
import com.github.thetric.iliasdownloader.service.model.CourseItem
import org.apache.http.client.fluent.Executor

interface CourseSyncService {
    Collection<Course> getJoinedCourses(Executor httpRequestExecutor)

    IliasService.VisitResult visit(CourseItem courseItem, Closure<IliasService.VisitResult> visitMethod, Executor httpRequestExecutor)
}
