package com.github.thetric.iliasdownloader.service.webparser.impl.util

import com.github.thetric.iliasdownloader.service.exception.IliasException
import groovy.transform.CompileStatic

@CompileStatic
final class WebIoExceptionTranslatorImpl implements WebIoExceptionTranslator {
    @Override
    IliasException translate(Exception e) {
        // TODO weiter ausbauen
        return new IliasException(e)
    }
}
