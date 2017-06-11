package com.github.thetric.iliasdownloader.cli

import com.github.thetric.iliasdownloader.cli.console.ConsoleService
import com.github.thetric.iliasdownloader.service.IliasItemVisitor
import com.github.thetric.iliasdownloader.service.IliasService
import com.github.thetric.iliasdownloader.service.model.Course
import com.github.thetric.iliasdownloader.ui.common.prefs.UserPreferenceService
import com.github.thetric.iliasdownloader.ui.common.prefs.UserPreferences
import groovy.transform.CompileStatic
import groovy.util.logging.Log4j2

/**
 * Updates the {@link UserPreferences} and executes the sync.
 *
 * @see UserPreferences
 * @see UserPreferenceService
 */
@Log4j2
@CompileStatic
final class SyncController {
    private final IliasService iliasService

    private final IliasItemVisitor iliasItemVisitor

    private final ResourceBundle resourceBundle
    private final UserPreferenceService preferenceService
    private final ConsoleService consoleService

    SyncController(
        final IliasService iliasService,
        final IliasItemVisitor iliasItemVisitor,
        final ResourceBundle resourceBundle,
        final UserPreferenceService preferenceService,
        final ConsoleService consoleService) {
        this.iliasService = iliasService
        this.iliasItemVisitor = iliasItemVisitor
        this.resourceBundle = resourceBundle
        this.preferenceService = preferenceService
        this.consoleService = consoleService
    }

    void startSync(final Collection<Course> courses) {
        printSelectedCourses(courses)
        executeSync(courses)
    }

    private void printSelectedCourses(final Collection<Course> coursesToSync) {
        final String courseList = coursesToSync*.name.join(', ')
        log.info("Syncing ${coursesToSync.size()} courses: $courseList")
    }

    private void executeSync(final Collection<Course> courses) {
        log.info(resourceBundle.getString('sync.started'))
        courses.each { iliasService.visit(it, iliasItemVisitor) }
        log.info(resourceBundle.getString('sync.finished'))
    }

}
