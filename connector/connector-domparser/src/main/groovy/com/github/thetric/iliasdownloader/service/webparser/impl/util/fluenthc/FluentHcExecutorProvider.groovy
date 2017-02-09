package com.github.thetric.iliasdownloader.service.webparser.impl.util.fluenthc

import groovy.transform.CompileStatic
import org.apache.http.client.fluent.Executor

@CompileStatic
interface FluentHcExecutorProvider {
    Executor createFluentHcExecutor(org.apache.http.client.CookieStore cookieStore)
}
