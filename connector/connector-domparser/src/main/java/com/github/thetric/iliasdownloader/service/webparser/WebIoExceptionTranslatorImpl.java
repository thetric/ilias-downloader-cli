package com.github.thetric.iliasdownloader.service.webparser;

import com.github.thetric.iliasdownloader.service.exception.IliasException;

/**
 * @author broj
 * @since 31.05.2016
 */
final class WebIoExceptionTranslatorImpl implements WebIoExceptionTranslator {
    @Override
    public IliasException translate(Exception e) {
        // TODO weiter ausbauen
        return new IliasException(e);
    }
}
