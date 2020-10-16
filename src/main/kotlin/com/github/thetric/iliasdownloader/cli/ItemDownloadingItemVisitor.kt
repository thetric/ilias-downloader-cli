package com.github.thetric.iliasdownloader.cli

import com.github.thetric.iliasdownloader.connector.api.ContextAwareIliasItemVisitor
import com.github.thetric.iliasdownloader.connector.api.IliasService
import com.github.thetric.iliasdownloader.connector.api.model.CourseFile
import com.github.thetric.iliasdownloader.connector.api.model.CourseFolder
import com.github.thetric.iliasdownloader.connector.api.model.IliasItem
import com.github.thetric.iliasdownloader.connector.domparser.impl.IliasHttpException
import mu.KotlinLogging
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.nio.file.attribute.FileTime
import java.text.DecimalFormat
import java.text.MessageFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import kotlin.math.log10
import kotlin.math.pow


private val systemZone = ZoneId.systemDefault()
private const val IEC_BASE = 1024L
private const val BYTES_PER_MEBIBYTE: Long = IEC_BASE * IEC_BASE
private val log = KotlinLogging.logger {}

/**
 * Downloads new or updated [CourseFile]s.
 */
class ItemDownloadingItemVisitor(
    private val iliasService: IliasService,
    private val bundle: ResourceBundle,
    maxFileSizeInMiB: Long
) : ContextAwareIliasItemVisitor<Path> {
    private val downloadSizeLimitInBytes: Long = if (maxFileSizeInMiB > 0) {
        toBytes(maxFileSizeInMiB)
    } else java.lang.Long.MAX_VALUE

    override fun handleFolder(parentContext: Path, folder: CourseFolder): Path {
        log.debug { getLocalizedMessage("sync.course.found", folder.name) }
        return resolvePathAndCreateMissingDirs(parentContext, folder)
    }

    override fun handleFile(parentContext: Path, file: CourseFile) {
        log.debug { getLocalizedMessage("sync.file.found", file.name) }
        val filePath = createItemPath(parentContext, file)
        if (needsToSync(filePath, file)) {
            log.info {
                getLocalizedMessage(
                    "sync.download.file.started",
                    file.name,
                    humanReadableFileSize(file.size)
                )
            }
            syncAndSaveFile(filePath, file)
        }
    }

    private fun createItemPath(
        parentContext: Path,
        item: IliasItem
    ) = parentContext.resolve(sanitizeFileName(item.name))

    @Suppress("SpreadOperator")
    private fun getLocalizedMessage(
        bundleKey: String,
        vararg msgArgs: Any
    ): String {
        return MessageFormat.format(bundle.getString(bundleKey), *msgArgs)
    }

    private fun resolvePathAndCreateMissingDirs(
        parentContext: Path,
        iliasItem: IliasItem
    ): Path {
        val itemPath = createItemPath(parentContext, iliasItem)
        return Files.createDirectories(itemPath)
    }

    private fun needsToSync(path: Path, file: CourseFile): Boolean {
        return (Files.notExists(path) ||
            isFileModified(path, file)) && isUnderFileLimit(file)
    }

    private fun isUnderFileLimit(file: CourseFile): Boolean {
        return file.size < downloadSizeLimitInBytes
    }

    private fun syncAndSaveFile(path: Path, file: CourseFile) {
        try {
            iliasService.getContentAsStream(file).use {
                Files.copy(it, path, StandardCopyOption.REPLACE_EXISTING)
            }
            Files.setLastModifiedTime(path, toFileTime(file.modified))
            log.info {
                getLocalizedMessage(
                    "sync.download.file.finished",
                    path.toUri()
                )
            }
        } catch (e: IliasHttpException) {
            // skip downloads with an unexpected HTTP status code (not 2xx).
            // in some rare cases (see issue #11) the webdav interface can
            // throw a 403 forbidden when accessing a file, although the user
            // can download the file via the 'normal' web interface.
            // other files in the course don't seem affected and can be
            // downloaded.
            val msg = getLocalizedMessage(
                "sync.download.file.failed",
                e.url,
                e.statusCode,
                file.name
            )
            log.error(msg)
            log.trace(msg, e)
        }
    }
}

/**
 * Removes all illegal characters from the given file name that are forbidden under Windows with NTFS.
 * For more information see [Wikipedia](https://en.wikipedia.org/wiki/NTFS).
 *
 * @param fileName
 * @return file name without invalid NTFS characters
 */
internal fun sanitizeFileName(fileName: String): String {
    return fileName.replace("""[\\/:*?"<>|]""".toRegex(), "")
}

private fun toFileTime(dateTime: LocalDateTime): FileTime {
    Objects.requireNonNull(dateTime, "dateTime")
    return FileTime.from(dateTime.toInstant(systemZone.rules.getOffset(dateTime)))
}

private fun toBytes(maxFileSizeInMiB: Long): Long {
    return (maxFileSizeInMiB * BYTES_PER_MEBIBYTE)
}

private fun isFileModified(path: Path, file: CourseFile): Boolean {
    val lastModifiedTime = Files.getLastModifiedTime(path)
    return lastModifiedTime != toFileTime(file.modified)
}

private val units = arrayOf("B", "kB", "MB", "GB", "TB")
private val fileSizeFormat = DecimalFormat("#,##0.#")

// based on https://stackoverflow.com/a/5599842
private fun humanReadableFileSize(size: Int): String {
    if (size <= 0) return "0"
    val iecBase = IEC_BASE.toDouble()
    val digitGroups: Int =
        (log10(size.toDouble()) / log10(iecBase)).toInt()
    return fileSizeFormat.format(
        size / iecBase.pow(digitGroups.toDouble())
    ) + " " + units[digitGroups]
}
