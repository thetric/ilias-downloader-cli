package com.github.thetric.iliasdownloader.connector.domparser.impl.course.jsoup

import org.jsoup.nodes.Document

/**
 * Abstraction for Jsoup's HTML parsing.
 */
interface JSoupParserService {
    fun parse(html: String): Document
}
