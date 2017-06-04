package com.github.thetric.iliasdownloader.cli.sync

import com.github.thetric.iliasdownloader.service.IliasItemVisitor
import com.github.thetric.iliasdownloader.service.IliasService
import com.github.thetric.iliasdownloader.service.model.CourseFile
import com.github.thetric.iliasdownloader.service.model.CourseFolder
import com.github.thetric.iliasdownloader.service.model.IliasItem
import com.github.thetric.iliasdownloader.ui.common.prefs.UserPreferences
import groovy.transform.CompileStatic
import groovy.transform.Memoized
import groovy.util.logging.Log4j2

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.nio.file.attribute.FileTime
import java.time.LocalDateTime
import java.time.ZoneId

import static com.github.thetric.iliasdownloader.service.IliasItemVisitor.VisitResult.CONTINUE

@CompileStatic
@Log4j2
final class ItemDownloadingItemVisitor implements IliasItemVisitor {
    private static final ZoneId SYSTEM_ZONE = ZoneId.systemDefault()
    private final Path basePath
    private final IliasService iliasService
    private final UserPreferences preferences

    private final long downloadSizeLimitInBytes

    ItemDownloadingItemVisitor(final Path basePath, final IliasService iliasService, final UserPreferences preferences) {
        this.basePath = basePath
        this.iliasService = iliasService
        this.preferences = preferences

        this.downloadSizeLimitInBytes = preferences.maxFileSizeInMiB > 0 ? toBytes(preferences) : Long.MAX_VALUE
    }

    private int toBytes(UserPreferences preferences) {
        return preferences.maxFileSizeInMiB * 1024 * 1024
    }

    private Path resolvePathAndCreateMissingDirs(IliasItem iliasItem) {
        Path parentItemPath = resolvePathOfParent(iliasItem.parent)
        Files.createDirectories parentItemPath
        return parentItemPath.resolve(sanitizeFileName(iliasItem.name))
    }

    @Memoized(maxCacheSize = 5)
    private Path resolvePathOfParent(IliasItem parentItem) {
        if (!parentItem) {
            return basePath
        }
        List<String> itemNamesInPath = []
        for (IliasItem i = parentItem; i; i = i.parent) {
            itemNamesInPath << i.name
        }
        Path itemPath = basePath
        for (String itemName : itemNamesInPath.reverse()) {
            itemPath = itemPath.resolve(sanitizeFileName(itemName))
        }
        return itemPath
    }

    /**
     * Removes all illegal characters from the given file name that are forbidden under Windows with NTFS.
     * For more information see <a href="https://en.wikipedia.org/wiki/NTFS">Wikipedia</a>.
     * @param fileName
     * @return file name without invalid NTFS characters
     */
    private String sanitizeFileName(String fileName) {
        fileName.replaceAll($/[\\/:*?"<>|]/$, '')
    }

    @Override
    IliasItemVisitor.VisitResult handleFolder(CourseFolder folder) {
        log.debug("Visiting folder '${folder.name}'")
        return CONTINUE
    }

    @Override
    IliasItemVisitor.VisitResult handleFile(CourseFile file) {
        log.debug("Visiting file ${file.name}")
        Path filePath = resolvePathAndCreateMissingDirs(file)
        if (needsToSync(filePath, file)) {
            log.info("Downloading file $file")
            syncAndSaveFile(filePath, file)
        }
        return CONTINUE
    }

    private boolean needsToSync(Path path, CourseFile file) {
        return (Files.notExists(path) || isFileModified(path, file)) && isUnderFileLimit(file)
    }

    private boolean isFileModified(Path path, CourseFile file) {
        FileTime lastModifiedTime = Files.getLastModifiedTime(path)
        return lastModifiedTime != toFileTime(file.modified)
    }

    private boolean isUnderFileLimit(CourseFile file) {
        return file.size < downloadSizeLimitInBytes
    }

    private void syncAndSaveFile(Path path, CourseFile file) {
        InputStream inputStream = iliasService.getContentAsStream(file)
        Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING)
        Files.setLastModifiedTime(path, toFileTime(file.modified))
    }

    private static FileTime toFileTime(LocalDateTime dateTime) {
        Objects.requireNonNull(dateTime, 'dateTime')
        FileTime.from(dateTime.toInstant(SYSTEM_ZONE.rules.getOffset(dateTime)))
    }
}
