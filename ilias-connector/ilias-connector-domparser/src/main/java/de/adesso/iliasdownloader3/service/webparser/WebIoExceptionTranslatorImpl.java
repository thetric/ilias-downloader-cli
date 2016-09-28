package de.adesso.iliasdownloader3.service.webparser;

import de.adesso.iliasdownloader3.service.exception.IliasException;

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
