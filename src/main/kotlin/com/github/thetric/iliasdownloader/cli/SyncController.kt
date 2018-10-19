package com.github.thetric.iliasdownloader.cli

import com.github.thetric.iliasdownloader.cli.sync.sanitizeFileName
import com.github.thetric.iliasdownloader.service.ContextAwareIliasItemVisitor
import com.github.thetric.iliasdownloader.service.IliasService
import com.github.thetric.iliasdownloader.service.model.Course
import com.github.thetric.iliasdownloader.ui.common.prefs.PreferenceService
import com.github.thetric.iliasdownloader.ui.common.prefs.UserPreferences
import mu.KotlinLogging
import java.nio.file.Path
import java.util.*

private val log = KotlinLogging.logger {}

/**
 * Updates the [UserPreferences] and executes the sync.
 *
 * @see UserPreferences
 *
 * @see PreferenceService
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
            coursesToSync.map { it.name }.joinToString(separator = ", ")
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
