package com.github.thetric.iliasdownloader.service.webparser.impl.course.jsoup

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

final class JSoupParserServiceImpl implements JSoupParserService {
    @Override
    Document parse(final String html) {
        return Jsoup.parse(html)
    }
}
