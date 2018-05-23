package com.github.thetric.iliasdownloader.cli.sync

import com.github.thetric.iliasdownloader.service.IliasItemVisitor
import com.github.thetric.iliasdownloader.service.IliasService
import com.github.thetric.iliasdownloader.service.model.CourseFile
import com.github.thetric.iliasdownloader.service.model.CourseFolder
import com.github.thetric.iliasdownloader.service.model.IliasItem
import com.github.thetric.iliasdownloader.service.webparser.impl.IliasHttpException
import mu.KotlinLogging
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.nio.file.attribute.FileTime
import java.text.DecimalFormat
import java.text.MessageFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import java.util.stream.Collectors
import java.util.stream.Stream


private val systemZone = ZoneId.systemDefault()
private const val IEC_BASE = 1024L
private const val BYTES_PER_MEBIBYTE: Long = IEC_BASE * IEC_BASE
private val log = KotlinLogging.logger {}

/**
 * Downloads new or updated [CourseFile]s.
 */
class ItemDownloadingItemVisitor(
    private val basePath: Path,
    private val iliasService: IliasService,
    private val bundle: ResourceBundle,
    maxFileSizeInMiB: Long
) : IliasItemVisitor {
    private val downloadSizeLimitInBytes: Long = if (maxFileSizeInMiB > 0) {
        toBytes(maxFileSizeInMiB)
    } else java.lang.Long.MAX_VALUE

    override fun handleFolder(folder: CourseFolder): IliasItemVisitor.VisitResult {
        log.debug { getLocalizedMessage("sync.course.found", folder.name) }
        return IliasItemVisitor.VisitResult.CONTINUE
    }

    override fun handleFile(file: CourseFile): IliasItemVisitor.VisitResult {
        log.debug { getLocalizedMessage("sync.file.found", file.name) }
        val filePath = resolvePathAndCreateMissingDirs(file)
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
        return IliasItemVisitor.VisitResult.CONTINUE
    }

    @Suppress("SpreadOperator")
    private fun getLocalizedMessage(
        bundleKey: String,
        vararg msgArgs: Any
    ): String {
        return MessageFormat.format(bundle.getString(bundleKey), *msgArgs)
    }

    private fun resolvePathAndCreateMissingDirs(iliasItem: IliasItem): Path {
        val parentItemPath = resolvePathOfParent(iliasItem.parent)
        Files.createDirectories(parentItemPath)
        return parentItemPath.resolve(sanitizeFileName(iliasItem.name))
    }

    private fun resolvePathOfParent(parentItem: IliasItem?): Path {
        val pathSegments =
            Stream.iterate(parentItem, Objects::nonNull, { it!!.parent })
                .map { sanitizeFileName(it!!.name) }
                .collect(Collectors.toList())
                .reversed()
                .joinToString(separator = File.separator)
        return basePath.resolve(pathSegments)
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
private fun sanitizeFileName(fileName: String): String {
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
        (Math.log10(size.toDouble()) / Math.log10(iecBase)).toInt()
    return fileSizeFormat.format(
        size / Math.pow(
            iecBase, digitGroups.toDouble()
        )
    ) + " " + units[digitGroups]
}
