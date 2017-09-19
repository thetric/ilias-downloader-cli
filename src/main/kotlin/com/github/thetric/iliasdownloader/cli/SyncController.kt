package com.github.thetric.iliasdownloader.cli

import com.github.thetric.iliasdownloader.service.IliasItemVisitor
import com.github.thetric.iliasdownloader.service.IliasService
import com.github.thetric.iliasdownloader.service.model.Course
import com.github.thetric.iliasdownloader.ui.common.prefs.PreferenceService
import com.github.thetric.iliasdownloader.ui.common.prefs.UserPreferences
import org.apache.logging.log4j.LogManager
import java.util.ResourceBundle

private val log = LogManager.getLogger(SyncController::class.java)

/**
 * Updates the [UserPreferences] and executes the sync.
 *
 * @see UserPreferences
 *
 * @see PreferenceService
 */
internal class SyncController(
    private val iliasService: IliasService,
    private val iliasItemVisitor: IliasItemVisitor,
    private val resourceBundle: ResourceBundle
) {

    fun startSync(courses: Collection<Course>) {
        printSelectedCourses(courses)
        executeSync(courses)
    }

    private fun printSelectedCourses(coursesToSync: Collection<Course>) {
        val courseList = coursesToSync.map { it.name }.joinToString(separator = ", ")
        log.info("Syncing ${coursesToSync.size} courses: $courseList")
    }

    private fun executeSync(courses: Collection<Course>) {
        log.info(resourceBundle.getString("sync.started"))
        courses.forEach {
            iliasService.visit(it, iliasItemVisitor)
        }
        log.info(resourceBundle.getString("sync.finished"))
    }
}
