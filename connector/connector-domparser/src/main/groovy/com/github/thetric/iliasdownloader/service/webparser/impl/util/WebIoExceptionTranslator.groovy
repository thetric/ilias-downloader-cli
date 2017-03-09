package com.github.thetric.iliasdownloader.service.webparser.impl.util

import com.github.thetric.iliasdownloader.service.exception.IliasException
import groovy.transform.CompileStatic

@CompileStatic
interface WebIoExceptionTranslator {
    IliasException translate(Exception e)
}
