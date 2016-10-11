package com.github.thetric.iliasdownloader.service.exception;

import lombok.Getter;

/**
 * @author broj
 * @since 09.10.2016
 */
public class CourseItemNotFoundException extends IliasException {
    private static final long serialVersionUID = 654654651649684L;
    @Getter
    private final String url;

    public CourseItemNotFoundException(String message, String url) {
        super(message + url);
        this.url = url;
    }
}
