package com.github.thetric.iliasdownloader.service.webparser.impl.course

import com.github.thetric.iliasdownloader.service.model.Course
import com.github.thetric.iliasdownloader.service.model.CourseItem
import org.apache.http.client.fluent.Executor

interface CourseSyncService {
    Collection<Course> getJoinedCourses(Executor httpRequestExecutor)

    Collection<? extends CourseItem> searchAllItems(Course course, Executor httpRequestExecutor)
}
