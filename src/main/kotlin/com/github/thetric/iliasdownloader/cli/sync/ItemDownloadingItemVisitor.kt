package com.github.thetric.iliasdownloader.cli.sync

import com.github.thetric.iliasdownloader.service.IliasItemVisitor
import com.github.thetric.iliasdownloader.service.IliasService
import com.github.thetric.iliasdownloader.service.model.CourseFile
import com.github.thetric.iliasdownloader.service.model.CourseFolder
import com.github.thetric.iliasdownloader.service.model.IliasItem
import mu.KotlinLogging
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.nio.file.attribute.FileTime
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import java.util.stream.Collectors
import java.util.stream.Stream

private val SYSTEM_ZONE = ZoneId.systemDefault()
private const val BYTES_PER_MEBIBYTE: Long = 1_048_576
private val log = KotlinLogging.logger {}

/**
 * Downloads new or updated [CourseFile]s.
 */
class ItemDownloadingItemVisitor(
    private val basePath: Path,
    private val iliasService: IliasService,
    maxFileSizeInMiB: Long
) : IliasItemVisitor {
    private val downloadSizeLimitInBytes: Long = if (maxFileSizeInMiB > 0) {
        toBytes(maxFileSizeInMiB)
    } else java.lang.Long.MAX_VALUE

    override fun handleFolder(folder: CourseFolder): IliasItemVisitor.VisitResult {
        log.debug { "Found folder \'${folder.name}\'" }
        return IliasItemVisitor.VisitResult.CONTINUE
    }

    override fun handleFile(file: CourseFile): IliasItemVisitor.VisitResult {
        log.debug { "Found file ${file.name}" }
        val filePath = resolvePathAndCreateMissingDirs(file)
        if (needsToSync(filePath, file)) {
            log.info { "Downloading file \'${file.name}\' (${formatBytes(file.size)} Bytes)" }
            syncAndSaveFile(filePath, file)
            log.info { "Saved to ${filePath.toUri()}" }
        }
        return IliasItemVisitor.VisitResult.CONTINUE
    }

    private fun resolvePathAndCreateMissingDirs(iliasItem: IliasItem): Path {
        val parentItemPath = resolvePathOfParent(iliasItem.parent)
        Files.createDirectories(parentItemPath)
        return parentItemPath.resolve(sanitizeFileName(iliasItem.name))
    }

    private fun resolvePathOfParent(parentItem: IliasItem?): Path {
        val pathSegments = Stream.iterate(parentItem, Objects::nonNull, { it!!.parent })
            .map { sanitizeFileName(it!!.name) }
            .collect(Collectors.toList())
            .reversed()
            .joinToString(separator = File.separator)
        return basePath.resolve(pathSegments)
    }

    private fun needsToSync(path: Path, file: CourseFile): Boolean {
        return (Files.notExists(path) || isFileModified(path, file)) && isUnderFileLimit(file)
    }

    private fun isUnderFileLimit(file: CourseFile): Boolean {
        return file.size < downloadSizeLimitInBytes
    }

    private fun syncAndSaveFile(path: Path, file: CourseFile) {
        iliasService.getContentAsStream(file).use {
            Files.copy(it, path, StandardCopyOption.REPLACE_EXISTING)
        }
        Files.setLastModifiedTime(path, toFileTime(file.modified))
    }
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

private fun toFileTime(dateTime: LocalDateTime): FileTime {
    Objects.requireNonNull(dateTime, "dateTime")
    return FileTime.from(dateTime.toInstant(SYSTEM_ZONE.rules.getOffset(dateTime)))
}

private fun toBytes(maxFileSizeInMiB: Long): Long {
    return (maxFileSizeInMiB * BYTES_PER_MEBIBYTE)
}

private fun isFileModified(path: Path, file: CourseFile): Boolean {
    val lastModifiedTime = Files.getLastModifiedTime(path)
    return lastModifiedTime != toFileTime(file.modified)
}

private fun formatBytes(bytes: Int): String {
    return NumberFormat.getIntegerInstance().format(bytes)
}
