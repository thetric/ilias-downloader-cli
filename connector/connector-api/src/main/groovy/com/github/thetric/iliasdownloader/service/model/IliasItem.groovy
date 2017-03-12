package com.github.thetric.iliasdownloader.service.model

import groovy.transform.CompileStatic

@CompileStatic
interface IliasItem {
    String getName()

    String getUrl()

    IliasItem getParent()
}
