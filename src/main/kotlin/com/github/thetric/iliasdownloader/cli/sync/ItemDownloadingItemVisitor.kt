package com.github.thetric.iliasdownloader.cli.sync

import com.github.thetric.iliasdownloader.service.IliasItemVisitor
import com.github.thetric.iliasdownloader.service.IliasService
import com.github.thetric.iliasdownloader.service.model.CourseFile
import com.github.thetric.iliasdownloader.service.model.CourseFolder
import com.github.thetric.iliasdownloader.service.model.IliasItem
import mu.KotlinLogging
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.nio.file.attribute.FileTime
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.ArrayList
import java.util.Objects

private val SYSTEM_ZONE = ZoneId.systemDefault()
private val BYTES_PER_MEBIBYTE: Long = 1_048_576
private val log = KotlinLogging.logger {}

/**
 * Downloads new or updated [CourseFile]s.
 */
class ItemDownloadingItemVisitor(
    private val basePath: Path,
    private val iliasService: IliasService,
    maxFileSizeInMiB: Long
) : IliasItemVisitor {
    private val downloadSizeLimitInBytes: Long

    init {
        this.downloadSizeLimitInBytes = if (maxFileSizeInMiB > 0) toBytes(maxFileSizeInMiB) else java.lang.Long.MAX_VALUE
    }

    private fun toBytes(maxFileSizeInMiB: Long): Long {
        return (maxFileSizeInMiB * BYTES_PER_MEBIBYTE)
    }

    private fun resolvePathAndCreateMissingDirs(iliasItem: IliasItem): Path {
        val parentItemPath = resolvePathOfParent(iliasItem.parent)
        Files.createDirectories(parentItemPath)
        return parentItemPath.resolve(sanitizeFileName(iliasItem.name))
    }

    //    @Memoized(maxCacheSize = 5)
    private fun resolvePathOfParent(parentItem: IliasItem?): Path {
        if (parentItem == null) {
            return basePath
        }

        val itemNamesInPath = ArrayList<String>()
        var i = parentItem
        while (i != null) {
            itemNamesInPath.add(i.name)
            i = i.parent
        }

        var itemPath = basePath
        for (itemName in itemNamesInPath.reversed()) {
            itemPath = itemPath.resolve(sanitizeFileName(itemName))
        }

        return itemPath
    }

    /**
     * Removes all illegal characters from the given file name that are forbidden under Windows with NTFS.
     * For more information see [Wikipedia](https://en.wikipedia.org/wiki/NTFS).
     *
     * @param fileName
     * @return file name without invalid NTFS characters
     */
    private fun sanitizeFileName(fileName: String): String {
        return fileName.replace("""[\\/:*?"<>|]""".toRegex(), "")
    }

    override fun handleFolder(folder: CourseFolder): IliasItemVisitor.VisitResult {
        log.debug { "Found folder \'${folder.name}\'" }
        return IliasItemVisitor.VisitResult.CONTINUE
    }

    override fun handleFile(file: CourseFile): IliasItemVisitor.VisitResult {
        log.debug { "Found file ${file.name}" }
        val filePath = resolvePathAndCreateMissingDirs(file)
        if (needsToSync(filePath, file)) {
            log.info { "Downloading file \'${file.name}\' (${file.size} Bytes)" }
            syncAndSaveFile(filePath, file)
            log.info { "Saved to ${filePath.toUri()}" }
        }
        return IliasItemVisitor.VisitResult.CONTINUE
    }

    private fun needsToSync(path: Path, file: CourseFile): Boolean {
        return (Files.notExists(path) || isFileModified(path, file)) && isUnderFileLimit(file)
    }

    private fun isFileModified(path: Path, file: CourseFile): Boolean {
        val lastModifiedTime = Files.getLastModifiedTime(path)
        return lastModifiedTime != toFileTime(file.modified)
    }

    private fun isUnderFileLimit(file: CourseFile): Boolean {
        return file.size < downloadSizeLimitInBytes
    }

    private fun syncAndSaveFile(path: Path, file: CourseFile) {
        val inputStream = iliasService.getContentAsStream(file)
        Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING)
        Files.setLastModifiedTime(path, toFileTime(file.modified))
    }


    private fun toFileTime(dateTime: LocalDateTime): FileTime {
        Objects.requireNonNull(dateTime, "dateTime")
        return FileTime.from(dateTime.toInstant(SYSTEM_ZONE.rules.getOffset(dateTime)))
    }
}
