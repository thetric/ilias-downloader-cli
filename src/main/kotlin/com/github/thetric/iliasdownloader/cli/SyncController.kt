package com.github.thetric.iliasdownloader.cli

import com.github.thetric.iliasdownloader.cli.preferences.UserPreferences
import com.github.thetric.iliasdownloader.connector.api.ContextAwareIliasItemVisitor
import com.github.thetric.iliasdownloader.connector.api.IliasService
import com.github.thetric.iliasdownloader.connector.api.model.Course
import mu.KotlinLogging
import java.nio.file.Path
import java.util.*

private val log = KotlinLogging.logger {}

/**
 * Updates the [UserPreferences] and executes the sync.
 *
 * @see UserPreferences
 * @see com.github.thetric.iliasdownloader.cli.preferences.JsonPreferenceService
 */
internal class SyncController(
    private val iliasService: IliasService,
    private val iliasItemVisitor: ContextAwareIliasItemVisitor<Path>,
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
            iliasService.visit(it, iliasItemVisitor, courseDir)
        }
        log.info(resourceBundle.getString("sync.finished"))
    }
}
