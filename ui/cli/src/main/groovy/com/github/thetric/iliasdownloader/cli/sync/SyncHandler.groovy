package com.github.thetric.iliasdownloader.cli.sync

import com.github.thetric.iliasdownloader.service.model.Course
import com.github.thetric.iliasdownloader.service.model.CourseFile
import com.github.thetric.iliasdownloader.service.model.CourseFolder
import com.github.thetric.iliasdownloader.service.model.IliasItem

interface SyncHandler {

    /**
     * Removes all illegal characters from the given file name that are forbidden under Windows with NTFS.
     * For more information see <a href="https://en.wikipedia.org/wiki/NTFS">Wikipedia</a>.
     * @param fileName
     * @return file name without invalid NTFS characters
     */
    String sanitizeFileName(String fileName)

    void handle(Course course)

    void handle(IliasItem courseItem)

    void handle(CourseFolder folder)

    void handle(CourseFile file)
}
