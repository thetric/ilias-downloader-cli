package com.github.thetric.iliasdownloader.service

import com.github.thetric.iliasdownloader.service.model.Course
import com.github.thetric.iliasdownloader.service.model.CourseFile
import com.github.thetric.iliasdownloader.service.model.CourseFolder
import com.github.thetric.iliasdownloader.service.model.CourseItem
import groovy.transform.CompileStatic
import groovy.util.logging.Log4j2

/**
 * Implements the <a href="https://en.wikipedia.org/wiki/Visitor_pattern">Visitor pattern</a> for
 * {@link com.github.thetric.iliasdownloader.service.model.IliasItem}s.
 *
 * @author thetric
 * @since 20.11.2016
 */
@Log4j2
@CompileStatic
class DefaultIliasItemVisitor {
    void visit(Course course) {
        log.info("Visiting course ${course}")
        course.items.each { visit(it) }
    }

    void visit(CourseItem courseItem) {
       log.warn("Unknown CourseItem type ${courseItem.class}")
    }

    void visit(CourseFolder folder) {
        log.info("Visiting folder ${folder}")
        folder.courseItems.each { visit(it) }
    }

    void visit(CourseFile file) {
        log.info("Found file ${file}")
    }
}
