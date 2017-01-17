package com.github.thetric.iliasdownloader.cli

import com.github.thetric.iliasdownloader.service.IliasService
import com.github.thetric.iliasdownloader.service.SyncingIliasItemVisitor
import com.github.thetric.iliasdownloader.service.model.Course
import groovy.util.logging.Log4j2
import io.reactivex.functions.Consumer

import java.nio.file.Path

/**
 * @author broj
 * @since 16.01.2017
 */
@Log4j2
abstract class AbstractCliController {
    Path basePath

    ResourceBundle resourceBundle

    def start() {
        IliasService iliasService = createIliasService()
        login(iliasService)


        println ''
        println 'Available courses:'
        Collection<Course> joinedCourses = iliasService.joinedCourses

        log.info(resourceBundle.getString('sync.started'))
        def itemVisitor = new SyncingIliasItemVisitor(basePath, iliasService)
        iliasService.searchCoursesWithContent(joinedCourses)
                    .subscribe({ itemVisitor.visit(it) } as Consumer)
        log.info(resourceBundle.getString('sync.finished'))
    }

    abstract IliasService createIliasService()

    abstract void login(IliasService iliasService)
}
