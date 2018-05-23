package com.github.thetric.iliasdownloader.cli

import com.github.thetric.iliasdownloader.cli.console.ConsoleService
import com.github.thetric.iliasdownloader.service.IliasService
import com.github.thetric.iliasdownloader.service.model.Course
import com.github.thetric.iliasdownloader.ui.common.prefs.PreferenceService
import com.github.thetric.iliasdownloader.ui.common.prefs.UserPreferences
import mu.KotlinLogging
import java.util.*

private val log = KotlinLogging.logger {}

/**
 * Updates the [UserPreferences]. It updates the file size limit and the courses
 * to sync.
 */
internal class UserPreferencesUpdateServiceImpl(
    private val iliasService: IliasService,
    private val resourceBundle: ResourceBundle,
    private val preferenceService: PreferenceService<UserPreferences>,
    private val consoleService: ConsoleService
) : UserPreferencesUpdateService {

    override fun updatePreferences(cliOptions: CliOptions): SyncSettings {
        val prefs = getPreferences(cliOptions)
        val (coursesToSync, newPrefs) =
            updateSyncCourses(cliOptions, prefs)
        if (newPrefs != prefs) {
            preferenceService.savePreferences(newPrefs)
        }
        return SyncSettings(coursesToSync, newPrefs.maxFileSizeInMiB)
    }

    private fun updateSyncCourses(
        cliOptions: CliOptions,
        prefs: UserPreferences
    ): Pair<Collection<Course>, UserPreferences> {
        val coursesFromIlias = iliasService.getJoinedCourses()
        val coursesToSync =
            getCoursesToSync(cliOptions, prefs, coursesFromIlias)
        // update ids - some might not exist anymore
        return Pair(
            coursesToSync,
            prefs.copy(activeCourses = coursesToSync.map { it.id }.distinct())
        )
    }

    private fun getCoursesToSync(
        cliOptions: CliOptions,
        prefs: UserPreferences,
        coursesFromIlias: Collection<Course>
    ): Collection<Course> {
        return if (cliOptions.showCourseSelection || prefs.activeCourses.isEmpty()) {
            try {
                return showAndSaveCourseSelection(coursesFromIlias.toList())
            } catch (e: CourseSelectionOutOfRange) {
                val errMsg =
                    resourceBundle.getString("sync.courses.prompt.errors.out-of-range")
                throw InvalidUsageException(
                    errMsg.replace(
                        "{0}",
                        coursesFromIlias.size.toString()
                    ), e
                )
            }
        } else {
            coursesFromIlias.filter { prefs.activeCourses.contains(it.id) }
        }
    }

    private fun getPreferences(cliOptions: CliOptions): UserPreferences {
        val prefs = preferenceService.loadPreferences()
        return updateFileSizeLimitFromCliOpts(prefs, cliOptions)
    }

    private fun updateFileSizeLimitFromCliOpts(
        prefs: UserPreferences,
        cliOptions: CliOptions
    ): UserPreferences {
        if (cliOptions.fileSizeLimitInMiB != null) {
            val newLimit = cliOptions.fileSizeLimitInMiB!!
            if (newLimit >= 0) {
                log.debug {
                    "New max file size limit (MiB): $newLimit, old was ${prefs.maxFileSizeInMiB}"
                }
                return prefs.copy(maxFileSizeInMiB = newLimit)
            } else {
                val errMsg =
                    "${resourceBundle.getString("args.sync.max-size.negative")} $newLimit"
                throw IllegalArgumentException(errMsg)
            }
        }
        return prefs
    }

    /**
     * Prompts the user to select the positions of the [Course]s to sync.
     * If the input is empty (default) all courses are selected.
     * Otherwise the space separated positions (1 based) are taken from the
     * `allCourses` argument.
     *
     * @param allCourses
     * [Course]s to select from
     * @return the [Course]s to sync
     */
    private fun showAndSaveCourseSelection(allCourses: List<Course>): Collection<Course> {
        log.info { ">>> ${resourceBundle.getString("sync.courses.available")}" }
        allCourses.forEachIndexed { index, (id, name) ->
            log.info { "\t${index + 1} $name (ID: $id)" }
        }
        val courseSelection = consoleService.readLine(
            "sync.courses",
            resourceBundle.getString("sync.courses.prompt")
        )
        val trimmedSelection = courseSelection.trim { it <= ' ' }
        if (trimmedSelection.isNotBlank()) {
            val courseIndices =
                courseSelection.split("""\s+""".toRegex()).map { it.toLong() }
                    .map { it - 1 }
            if (courseIndices.any { it < 0 || it >= allCourses.size }) {
                throw CourseSelectionOutOfRange()
            }
            return courseIndices.map { allCourses[it.toInt()] }
        }

        return allCourses
    }

    private class CourseSelectionOutOfRange : RuntimeException()
}
