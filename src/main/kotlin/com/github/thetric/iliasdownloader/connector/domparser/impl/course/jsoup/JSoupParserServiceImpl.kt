package com.github.thetric.iliasdownloader.connector.domparser.impl.course.jsoup

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

/**
 *
 */
class JSoupParserServiceImpl : JSoupParserService {
    override fun parse(html: String): Document {
        return Jsoup.parse(html)
    }
}
