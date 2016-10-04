package com.github.thetric.iliasdownloader.service.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Collection;

/**
 * @author broj
 * @since 18.08.2016
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class CourseFolder extends AbstractIliasItem implements CourseItem {
    @Getter
    private final Collection<? extends CourseItem> courseItems;

    public CourseFolder(int id, String name, String url, Collection<? extends CourseItem> courseItems) {
        super(id, name, url);
        this.courseItems = courseItems;
    }
}
