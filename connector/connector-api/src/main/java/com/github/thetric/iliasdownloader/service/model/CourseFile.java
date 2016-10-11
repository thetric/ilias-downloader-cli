package com.github.thetric.iliasdownloader.service.model;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author broj
 * @since 18.08.2016
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class CourseFile extends AbstractIliasItem implements CourseItem {
    public CourseFile(int id, String name, String url) {
        super(id, name, url);
    }
}
