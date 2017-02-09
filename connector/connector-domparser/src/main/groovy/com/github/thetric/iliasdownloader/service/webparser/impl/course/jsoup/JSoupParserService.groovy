package com.github.thetric.iliasdownloader.service.webparser.impl.course.jsoup

import org.jsoup.nodes.Document

interface JSoupParserService {
    Document parse(String html)
}
