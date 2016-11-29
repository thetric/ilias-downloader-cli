package com.github.thetric.iliasdownloader.service.webparser;

import com.github.thetric.iliasdownloader.service.exception.IliasException
import groovy.transform.CompileStatic;

/**
 * @author broj
 * @since 31.05.2016
 */
@CompileStatic
final class WebIoExceptionTranslatorImpl implements WebIoExceptionTranslator {
    @Override
    IliasException translate(Exception e) {
        // TODO weiter ausbauen
        return new IliasException(e);
    }
}
