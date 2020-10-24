package com.github.thetric.iliasdownloader.cli

import com.github.thetric.iliasdownloader.cli.preferences.UserPreferences
import com.github.thetric.iliasdownloader.connector.IliasItemListener
import com.github.thetric.iliasdownloader.connector.IliasService
import com.github.thetric.iliasdownloader.connector.model.Course
import mu.KotlinLogging
import java.nio.file.Files
import java.nio.file.Path
import java.util.ResourceBundle

private val log = KotlinLogging.logger {}

/**
 * Updates the [UserPreferences] and executes the sync.
 *
 * @see UserPreferences
 * @see com.github.thetric.iliasdownloader.cli.preferences.JsonPreferenceService
 */
internal class SyncController(
    private val iliasService: IliasService,
    private val iliasItemListener: IliasItemListener<Path>,
    private val resourceBundle: ResourceBundle,
    private val syncBaseDir: Path
) {

    fun startSync(courses: Collection<Course>) {
        printSelectedCourses(courses)
        executeSync(courses)
    }

    private fun printSelectedCourses(coursesToSync: Collection<Course>) {
        val courseList =
            coursesToSync.joinToString(separator = ", ") { it.name }
        log.info { "Syncing ${coursesToSync.size} courses: $courseList" }
    }

    private fun executeSync(courses: Collection<Course>) {
        log.info(resourceBundle.getString("sync.started"))
        courses.forEach {
            val courseDir = syncBaseDir.resolve(sanitizeFileName(it.name))
            Files.createDirectories(courseDir)
            iliasService.visit(it, iliasItemListener, courseDir)
        }
        log.info(resourceBundle.getString("sync.finished"))
    }
}
