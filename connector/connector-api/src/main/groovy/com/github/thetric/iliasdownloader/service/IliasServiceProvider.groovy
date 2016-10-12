package com.github.thetric.iliasdownloader.service

import groovy.transform.CompileStatic

/**
 * @author broj
 * @since 25.09.2016
 */
@CompileStatic
interface IliasServiceProvider {
    IliasService newInstance()
}
