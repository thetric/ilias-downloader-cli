package de.adesso.iliasdownloader3.service.model;

import lombok.Getter;

import java.util.Collection;

/**
 * @author broj
 * @since 18.08.2016
 */
public final class CourseFolder extends AbstractIliasItem implements CourseItem {
    @Getter
    private final Collection<? extends CourseItem> courseItems;

    public CourseFolder(int id, String name, String url, Collection<? extends CourseItem> courseItems) {
        super(id, name, url);
        this.courseItems = courseItems;
    }
}
