package com.github.thetric.iliasdownloader.service

import groovy.transform.CompileStatic

/**
 * Encapsulates the creation of {@link IliasService}s.
 * All implementations are encouraged to implement this to provide {@link IliasService}s.
 */
@CompileStatic
interface IliasServiceProvider {
    /**
     * Creates a new {@link IliasService}.
     * @return a new {@link IliasService}
     */
    IliasService newInstance()
}
