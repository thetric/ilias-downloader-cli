package com.github.thetric.iliasdownloader.cli

import com.github.thetric.iliasdownloader.service.IliasService
import com.github.thetric.iliasdownloader.service.SyncingIliasItemVisitor
import com.github.thetric.iliasdownloader.service.model.Course
import com.github.thetric.iliasdownloader.ui.common.prefs.UserPreferenceService
import groovy.transform.TupleConstructor
import groovy.util.logging.Log4j2
import io.reactivex.functions.Consumer
import org.apache.logging.log4j.Level

import java.nio.file.NoSuchFileException
import java.nio.file.Path
import java.util.function.Function

/**
 * @author broj
 * @since 16.01.2017
 */
@Log4j2
@TupleConstructor
final class IliasCliController {
    Path basePath

    Function<String, IliasService> iliasProvider
    ResourceBundle resourceBundle
    UserPreferenceService preferenceService
    ConsoleService consoleService

    def start() {
        def iliasService
        try {
            log.info('check for existing config in {}', basePath.toAbsolutePath())
            iliasService = new ExistingConfigCliController(
                iliasProvider,
                resourceBundle,
                preferenceService,
                consoleService).start()
        } catch (NoSuchFileException settingsNotFoundEx) {
            log.warn('no config found in {}', basePath.toAbsolutePath())
            log.catching(Level.DEBUG, settingsNotFoundEx)
            iliasService = new SetupController(iliasProvider, resourceBundle, preferenceService, consoleService).
                startSetup()
        }

        println ''
        println 'Available courses:'
        Collection<Course> joinedCourses = iliasService.joinedCourses

        log.info(resourceBundle.getString('sync.started'))
        def itemVisitor = new SyncingIliasItemVisitor(basePath, iliasService)
        iliasService.searchCoursesWithContent(joinedCourses)
                    .subscribe({ itemVisitor.visit(it) } as Consumer)
        log.info(resourceBundle.getString('sync.finished'))
    }
}
