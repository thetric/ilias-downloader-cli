package com.github.thetric.iliasdownloader.cli

import com.github.thetric.iliasdownloader.service.IliasService
import com.github.thetric.iliasdownloader.service.model.Course
import com.github.thetric.iliasdownloader.service.model.IliasItem
import com.github.thetric.iliasdownloader.ui.common.prefs.UserPreferenceService
import groovy.transform.TupleConstructor
import groovy.util.logging.Log4j2
import org.apache.logging.log4j.Level

import java.nio.file.NoSuchFileException
import java.util.function.Function

/**
 * @author broj
 * @since 16.01.2017
 */
@Log4j2
@TupleConstructor
final class IliasCliController {
    CliOptions cliOptions

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
        joinedCourses.each { println it.name }

        log.info(resourceBundle.getString('sync.started'))
        SyncHandler syncHandler = new SyncHandlerImpl(cliOptions.syncDir, iliasService)
        for (Course course : joinedCourses) {
            if (shouldTerminate()) break
            iliasService.visit(course, { IliasItem iliasItem ->
                syncHandler.handle(iliasItem)
            })
        }
        log.info(resourceBundle.getString('sync.finished'))
    }

    private boolean shouldTerminate() {
        // TODO
        false
    }
}
