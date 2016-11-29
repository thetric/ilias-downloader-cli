package com.github.thetric.iliasdownloader.service

import com.github.thetric.iliasdownloader.service.model.*
import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import groovy.transform.TupleConstructor
import groovy.util.logging.Log4j2
import io.reactivex.functions.Consumer

import java.nio.file.Files
import java.nio.file.Path
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
@CompileStatic
@TupleConstructor
final class SyncingIliasItemVisitor {
    private static final ZoneId SYSTEM_ZONE = ZoneId.systemDefault()
    private Path basePath
    private IliasService iliasService

    // TODO strategy for handling exceptions: simple java.util.function.Supplier or complexer class?

    @PackageScope
    Path resolvePathAndCreateMissingDirs(Path root, IliasItem iliasItem) {
        def coursePath = root.resolve(sanitizeFileName(iliasItem.name))
        Files.createDirectories coursePath
    }

    /**
     * Removes all illegal characters from the given file name that are forbidden under Windows with NTFS.
     * For more information see <a href="https://en.wikipedia.org/wiki/NTFS">Wikipedia</a>.
     * @param fileName
     * @return file name without invalid NTFS characters
     */
    String sanitizeFileName(String fileName) {
        fileName.replaceAll($/[\\/:*?"<>|]/$, '')
    }

    void visit(Course course) {
        log.info("Visiting course ${course}")
        def coursePath = resolvePathAndCreateMissingDirs(basePath, course)
        course.items.each { visit(coursePath, it) }
    }

    void visit(Path basePath, CourseItem courseItem) {
        log.warn("Unknown CourseItem type ${courseItem.class}: $courseItem")
    }

    void visit(Path basePath, CourseFolder folder) {
        log.info("Visiting folder ${folder}")
        def folderPath = resolvePathAndCreateMissingDirs(basePath, folder)
        folder.courseItems.each { visit(folderPath, it) }
    }

    void visit(Path basePath, CourseFile file) {
        log.info("Download file $file")
        def filePath = basePath.resolve(sanitizeFileName(file.name))
        if (needsToSync(filePath, file)) {
            syncAndSaveFile(filePath, file)
        }
    }

    @PackageScope
    boolean needsToSync(Path path, CourseFile file) {
        if (Files.notExists(path)) {
            return true
        } else {
            def lastModifiedTime = Files.getLastModifiedTime(path)
            def modDateTime = LocalDateTime.ofInstant(lastModifiedTime.toInstant(), SYSTEM_ZONE)
            return modDateTime != file.modified
        }
    }

    @PackageScope
    void syncAndSaveFile(Path path, CourseFile file) {
        iliasService.getContent(file).subscribe(new Consumer<byte[]>() {
            @Override
            void accept(byte[] bytes) {
                Files.newOutputStream(path).withObjectOutputStream { it.write(bytes) }
            }
        })
    }
}
