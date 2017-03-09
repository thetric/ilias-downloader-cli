package com.github.thetric.iliasdownloader.service.model

import groovy.transform.CompileStatic

@CompileStatic
interface IliasItem {
    int getId()

    String getName()

    String getUrl()

    IliasItem getParent()
}
