package com.github.thetric.iliasdownloader.service.webparser.impl

import com.github.thetric.iliasdownloader.service.IliasItemVisitor
import com.github.thetric.iliasdownloader.service.model.Course
import com.github.thetric.iliasdownloader.service.model.CourseFile
import com.github.thetric.iliasdownloader.service.model.LoginCredentials
import com.github.thetric.iliasdownloader.service.webparser.impl.course.CourseSyncService
import com.github.thetric.iliasdownloader.service.webparser.impl.webclient.IliasWebClient
import spock.lang.Specification

class WebIliasServiceTest extends Specification {
    private final CourseSyncService courseSyncService = Mock()
    private final IliasWebClient iliasWebClient = Mock()

    private final WebIliasService sut = new WebIliasService(courseSyncService, iliasWebClient)

    def "getContentAsStream: call webClient with correct arg"() {
        setup:
        final url = 'https://ilias.de/file/url/bar.pdf'
        final CourseFile file = new CourseFile('Dummy file', url, null, null, 42)

        when:
        sut.getContentAsStream(file)

        then:
        1 * iliasWebClient.getAsInputStream(url)
    }

    def "login: call webClient with credentials"() {
        setup:
        final credentials = new LoginCredentials(userName: 'foo', password: 'secret1')

        when:
        sut.login(credentials)

        then:
        1 * iliasWebClient.login(credentials)
    }

    def "logout: call webClient.logout"() {
        when:
        sut.logout()

        then:
        1 * iliasWebClient.logout()
    }

    def "getJoinedCourses: return courses from course sync srvc"() {
        setup:
        final def expected = Mock(Collection)
        courseSyncService.joinedCourses >> expected

        expect:
        sut.joinedCourses == expected
    }

    def "visit: pass through args"() {
        setup:
        final Course course = new Course(id: 684, name: 'Web Engineering', url: 'https://fh.de/ilias/we/46', parent: null)
        final IliasItemVisitor visitMeth = Mock()

        when:
        sut.visit(course, visitMeth)

        then:
        1 * courseSyncService.visit(course, visitMeth)
    }
}
