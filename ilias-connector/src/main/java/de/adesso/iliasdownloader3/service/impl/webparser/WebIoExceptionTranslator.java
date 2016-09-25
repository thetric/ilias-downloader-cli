package de.adesso.iliasdownloader3.service.impl.webparser;

import de.adesso.iliasdownloader3.service.exception.IliasException;

/**
 * @author broj
 * @since 31.05.2016
 */
interface WebIoExceptionTranslator {
    IliasException translate(Exception e);
}
