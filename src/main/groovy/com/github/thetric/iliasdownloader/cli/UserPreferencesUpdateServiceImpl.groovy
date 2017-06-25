package com.github.thetric.iliasdownloader.cli

import com.github.thetric.iliasdownloader.cli.console.ConsoleService
import com.github.thetric.iliasdownloader.service.IliasService
import com.github.thetric.iliasdownloader.service.model.Course
import com.github.thetric.iliasdownloader.ui.common.prefs.PreferenceService
import com.github.thetric.iliasdownloader.ui.common.prefs.UserPreferences
import groovy.transform.CompileStatic
import groovy.util.logging.Log4j2

import java.util.stream.Collectors

/**
 * Updates the {@link UserPreferences}. It updates the file size limit and the courses to sync.
 */
@Log4j2
@CompileStatic
final class UserPreferencesUpdateServiceImpl implements UserPreferencesUpdateService {
    private final IliasService iliasService

    private final ResourceBundle resourceBundle
    private final PreferenceService<UserPreferences> preferenceService
    private final ConsoleService consoleService

    UserPreferencesUpdateServiceImpl(
        final IliasService iliasService,
        final ResourceBundle resourceBundle, final PreferenceService<UserPreferences> preferenceService,
        final ConsoleService consoleService) {
        this.iliasService = iliasService
        this.resourceBundle = resourceBundle
        this.preferenceService = preferenceService
        this.consoleService = consoleService
    }

    @Override
    SyncSettings updatePreferences(final CliOptions cliOptions) {
        final UserPreferences prefs = preferenceService.loadPreferences()

        updateFileSizeLimitFromCliOpts(prefs, cliOptions)

        final Collection<Course> coursesFromIlias = iliasService.joinedCourses
        final Collection<Course> coursesToSync = getCoursesToSync(cliOptions, prefs, coursesFromIlias)
        // update ids - some might not exist anymore
        prefs.activeCourses = coursesToSync.stream()
                                           .map { it.id as Long }
                                           .distinct()
                                           .collect(Collectors.<Long> toList())
        preferenceService.savePreferences(prefs)
        return new SyncSettings(coursesToSync, prefs.maxFileSizeInMiB)
    }

    private Collection<Course> getCoursesToSync(
        final CliOptions cliOptions, final UserPreferences prefs, final Collection<Course> coursesFromIlias) {
        if (cliOptions.showCourseSelection || !prefs.activeCourses) {
            try {
                return showAndSaveCourseSelection(coursesFromIlias)
            } catch (final CourseSelectionOutOfRange e) {
                final String errMsg = resourceBundle.getString('sync.courses.prompt.errors.out-of-range')
                throw new InvalidUsageException(errMsg.replace('{0}', coursesFromIlias.size() as String), e)
            }
        } else {
            return coursesFromIlias.findAll { prefs.activeCourses.contains(it.id) }
        }
    }

    private void updateFileSizeLimitFromCliOpts(final UserPreferences prefs, final CliOptions cliOptions) {
        if (cliOptions.fileSizeLimitInMiB != null) {
            // limit = 0 -> unlimited
            if (cliOptions.fileSizeLimitInMiB >= 0) {
                log.debug('New max file size limit (MiB): {}, old was {}', cliOptions.fileSizeLimitInMiB, prefs.maxFileSizeInMiB)
                prefs.maxFileSizeInMiB = cliOptions.fileSizeLimitInMiB
                preferenceService.savePreferences(prefs)
            } else {
                final GString errMsg = "${resourceBundle.getString('args.sync.max-size.negative')} $cliOptions.fileSizeLimitInMiB"
                throw new IllegalArgumentException(errMsg)
            }
        }
    }

    /**
     * Prompts the user to select the positions of the {@link Course}s to sync. If the input is empty (default) all
     * courses are selected. Otherwise the space separated positions (1 based) are taken from the {@code allCourses} argument.
     * @param allCourses {@link Course}s to select from
     * @return the {@link Course}s to sync
     */
    private Collection<Course> showAndSaveCourseSelection(final Collection<Course> allCourses) {
        log.info ">>> ${resourceBundle.getString('sync.courses.available')}"
        allCourses.eachWithIndex { final Course course, final int i ->
            log.info "\t${i + 1} ${course.name} (ID: ${course.id})"
        }
        final String courseSelection = consoleService.readLine('sync.courses', resourceBundle.getString('sync.courses.prompt'))
        final trimmedSelection = courseSelection.trim()
        if (trimmedSelection) {
            final List<Integer> courseIndices = courseSelection.split(/\s+/)
                                                               .collect { final String it -> Integer.parseInt it }
                                                               .collect { it - 1 }
            if (courseIndices.any { it < 0 || it > allCourses.size() }) {
                throw new CourseSelectionOutOfRange()
            }
            return courseIndices.collect { allCourses[it] }
        }
        return allCourses
    }

    private static final class CourseSelectionOutOfRange extends RuntimeException {

    }
}
