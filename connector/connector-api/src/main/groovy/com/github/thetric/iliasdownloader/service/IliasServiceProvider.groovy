package com.github.thetric.iliasdownloader.service

import groovy.transform.CompileStatic

@CompileStatic
interface IliasServiceProvider {
    IliasService newInstance()
}
