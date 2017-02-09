package com.github.thetric.iliasdownloader.service.webparser.impl.course

import com.github.thetric.iliasdownloader.service.model.Course
import io.reactivex.Observable
import org.apache.http.client.fluent.Executor

interface CourseSyncService {
    Collection<Course> getJoinedCourses(Executor httpRequestExecutor)

    Observable<Course> searchCoursesWithContent(Collection<Course> selectedCourses, Executor httpRequestExecutor)
}
