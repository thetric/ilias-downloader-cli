package com.github.thetric.iliasdownloader.service

import com.github.thetric.iliasdownloader.service.model.*
import groovy.transform.PackageScope
import groovy.transform.TupleConstructor
import groovy.util.logging.Log4j2
import io.reactivex.functions.Consumer

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.FileTime
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Implements the <a href="https://en.wikipedia.org/wiki/Visitor_pattern">Visitor pattern</a> for
 * {@link com.github.thetric.iliasdownloader.service.model.IliasItem}s.
 *
 * @author thetric
 * @since 20.11.2016
 */
@Log4j2
@TupleConstructor
final class SyncingIliasItemVisitor {
    private static final ZoneId SYSTEM_ZONE = ZoneId.systemDefault()
    private Path basePath
    private IliasService iliasService

    // TODO strategy for handling exceptions: simple java.util.function.Supplier or complexer class?

    @PackageScope
    static Path resolvePathAndCreateMissingDirs(Path root, IliasItem iliasItem) {
        def coursePath = root.resolve(sanitizeFileName(iliasItem.name))
        Files.createDirectories coursePath
    }

    /**
     * Removes all illegal characters from the given file name that are forbidden under Windows with NTFS.
     * For more information see <a href="https://en.wikipedia.org/wiki/NTFS">Wikipedia</a>.
     * @param fileName
     * @return file name without invalid NTFS characters
     */
    static String sanitizeFileName(String fileName) {
        fileName.replaceAll($/[\\/:*?"<>|]/$, '')
    }

    void visit(Course course) {
        log.info("Visiting course '${course.name}' (id: ${course.id}")
        def coursePath = resolvePathAndCreateMissingDirs(basePath, course)
        course.items.each { visit(coursePath, it) }
    }

    void visit(Path basePath, CourseItem courseItem) {
        log.warn("Unknown CourseItem type ${courseItem.class}: $courseItem")
    }

    void visit(Path basePath, CourseFolder folder) {
        log.info("Visiting folder '${folder.name}'")
        def folderPath = resolvePathAndCreateMissingDirs(basePath, folder)
        folder.courseItems.each { visit(folderPath, it) }
    }

    void visit(Path basePath, CourseFile file) {
        log.info("Visiting file ${file.name}")
        def filePath = basePath.resolve(sanitizeFileName(file.name))
        if (needsToSync(filePath, file)) {
            log.info("Downloading file $file")
            syncAndSaveFile(filePath, file)
        }
    }

    @PackageScope
    static boolean needsToSync(Path path, CourseFile file) {
        if (Files.notExists(path)) {
            return true
        } else {
            def lastModifiedTime = Files.getLastModifiedTime(path)
            return lastModifiedTime != toFileTime(file.modified)
        }
    }

    @PackageScope
    void syncAndSaveFile(Path path, CourseFile file) {
        iliasService.getContentAsStream(file)
                    .subscribe(new Consumer<InputStream>() {
            @Override
            void accept(InputStream inputStream) throws Exception {
                Files.copy(inputStream, path)
                Files.setLastModifiedTime(path, toFileTime(file.modified))
            }
        })
    }

    @PackageScope
    static FileTime toFileTime(LocalDateTime dateTime) {
        Objects.requireNonNull(dateTime, 'dateTime')
        FileTime.from(dateTime.toInstant(SYSTEM_ZONE.rules.getOffset(dateTime)))
    }
}
