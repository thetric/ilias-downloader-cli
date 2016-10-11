package com.github.thetric.iliasdownloader.service.webparser;

import com.github.thetric.iliasdownloader.service.exception.IliasException;

/**
 * @author broj
 * @since 31.05.2016
 */
interface WebIoExceptionTranslator {
    IliasException translate(Exception e);
}
