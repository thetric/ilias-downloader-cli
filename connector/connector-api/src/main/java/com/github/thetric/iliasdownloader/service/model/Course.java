package com.github.thetric.iliasdownloader.service.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author broj
 * @since 31.05.2016
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class Course extends AbstractIliasItem {
    private final Collection<? extends CourseItem> items;

    public Course(int id, String name, String url, Collection<? extends CourseItem> items) {
        super(id, name, url);
        this.items = items;
    }

    public Course(int id, String name, String url) {
        this(id, name, url, new ArrayList<>());
    }
}
