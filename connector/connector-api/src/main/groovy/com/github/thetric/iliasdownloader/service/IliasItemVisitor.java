package com.github.thetric.iliasdownloader.service;

import com.github.thetric.iliasdownloader.service.model.Course;
import com.github.thetric.iliasdownloader.service.model.CourseFile;
import com.github.thetric.iliasdownloader.service.model.CourseFolder;

/**
 * Callback interface for the {@link IliasService#visit(Course, IliasItemVisitor)}.
 */
public interface IliasItemVisitor {
    enum VisitResult {
        CONTINUE, TERMINATE
    }

    default VisitResult handleFolder(final CourseFolder folder) {
        return VisitResult.CONTINUE;
    }

    VisitResult handleFile(final CourseFile file);
}
