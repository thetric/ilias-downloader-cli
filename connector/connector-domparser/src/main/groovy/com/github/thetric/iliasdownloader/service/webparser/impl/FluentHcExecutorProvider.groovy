package com.github.thetric.iliasdownloader.service.webparser.impl

import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import org.apache.http.client.fluent.Executor

@PackageScope
@CompileStatic
interface FluentHcExecutorProvider {
    Executor createFluentHcExecutor(org.apache.http.client.CookieStore cookieStore)
}
