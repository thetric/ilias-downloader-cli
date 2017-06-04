package com.github.thetric.iliasdownloader.service.webparser.impl.course

import com.github.thetric.iliasdownloader.service.model.Course
import com.github.thetric.iliasdownloader.service.webparser.impl.IliasItemIdStringParsingException
import com.github.thetric.iliasdownloader.service.webparser.impl.course.jsoup.JSoupParserService
import com.github.thetric.iliasdownloader.service.webparser.impl.webclient.IliasWebClient
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import spock.lang.Specification

class CourseSyncServiceImplTest extends Specification {
    private final JSoupParserService jSoupParserService = Mock()
    private final IliasWebClient webClient = Mock()

    private final String iliasBaseUrl = 'https://www.ilias.fh-dortmund.de/ilias/'
    private final String clientId = 'ilias-fhdo'

    private final courseOverview = "${iliasBaseUrl}ilias.php?baseClass=ilPersonalDesktopGUI&cmd=jumpToSelectedItems"
    private final courseLinkPrefix = "${iliasBaseUrl}goto_${clientId}_crs_"
    private final courseWebDavPrefix = "${iliasBaseUrl}webdav.php/ilias-fhdo/ref_"

    private final CourseSyncServiceImpl sut = new CourseSyncServiceImpl(
        jSoupParserService,
        webClient,
        iliasBaseUrl,
        clientId)

    def "getJoinedCourses: returns empty list if no courses can be found"() {
        setup:
        final String html = 'the html from the course overview'
        final Document doc = Stub(constructorArgs: [iliasBaseUrl])

        when:
        final def actual = sut.joinedCourses

        then:
        1 * webClient.getHtml(courseOverview) >> html
        1 * jSoupParserService.parse(html) >> doc
        doc.select(_) >> []

        actual == []
    }

    def "getJoinedCourses: return one course"() {
        setup:
        final String html = 'the html from the course overview'
        final Document doc = Stub(constructorArgs: [iliasBaseUrl])
        final Element courseElement = Stub(constructorArgs: ['a'])
        final courseTitle = 'course title'
        final courseId = 2612
        courseElement.text() >> courseTitle
        courseElement.attr('href') >> "https://www.ilias.fh-dortmund.de/ilias/goto_ilias-fhdo_crs_${courseId}.html"

        when:
        final def actual = sut.joinedCourses

        then:
        1 * webClient.getHtml(courseOverview) >> html
        1 * jSoupParserService.parse(html) >> doc
        doc.select(_) >> [courseElement]

        actual == [new Course(courseId, courseTitle, "$courseWebDavPrefix$courseId/", null)]
    }

    def "getJoinedCourses: throw err if course url could not be parsed"() {
        setup:
        final String html = 'the html from the course overview'
        final Document doc = Stub(constructorArgs: [iliasBaseUrl])
        final Element courseElement = Stub(constructorArgs: ['a'])
        final courseTitle = 'course title'
        final courseId = 2612
        courseElement.text() >> courseTitle
        courseElement.attr('href') >> "https://www.ilias.fh-dortmund.de/ilias/COURSE-LINK_${courseId}.jsf"

        1 * webClient.getHtml(courseOverview) >> html
        1 * jSoupParserService.parse(html) >> doc
        doc.select(_) >> [courseElement]

        when:
        sut.joinedCourses

        then:
        thrown IliasItemIdStringParsingException
    }

}
