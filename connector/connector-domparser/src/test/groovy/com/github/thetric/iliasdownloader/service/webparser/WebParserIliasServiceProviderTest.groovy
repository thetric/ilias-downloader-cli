package com.github.thetric.iliasdownloader.service.webparser

import com.github.thetric.iliasdownloader.service.webparser.impl.CookieNotFoundException
import spock.lang.Specification
import spock.lang.Unroll

class WebParserIliasServiceProviderTest extends Specification {
    private final CookieService cookieService = Stub()

    def "constructor: throw err if login page is null"() {
        when:
        new WebParserIliasServiceProvider(cookieService, null)

        then:
        thrown IllegalArgumentException
    }

    def "constructor: throw err if login page contains only whitespaces"() {
        when:
        new WebParserIliasServiceProvider(cookieService, '''
''')

        then:
        thrown IllegalArgumentException
    }

    def "constructor: throw if loginUrl does not contain 'login.php'"() {
        when:
        new WebParserIliasServiceProvider(cookieService, 'https://www.ilias.fh-dortmund.de/ilias/ilias.php?lang=en')

        then:
        thrown IllegalArgumentException
    }

    def "constructor: rethrow IOEx from cookieService"() {
        setup:
        final Exception originalEx = new IOException('')
        cookieService.getCookieFromUrl(_, _) >> { throw originalEx }

        when:
        new WebParserIliasServiceProvider(cookieService, 'https://www.ilias.fh-dortmund.de/ilias/login.php')

        then:
        final def e = thrown IOException
        e.cause == originalEx
    }

    def "constructor: throw NoCookiesAvailableException if cookie cannot be found"() {
        setup:
        cookieService.getCookieFromUrl(_, _) >> null

        when:
        new WebParserIliasServiceProvider(cookieService, 'https://www.ilias.fh-dortmund.de/ilias/login.php')

        then:
        thrown CookieNotFoundException
    }

    def "constructor: save client id"() {
        setup:
        final def clientId = 'client-id'
        final def loginUrl = 'https://www.ilias.fh-dortmund.de/ilias/login.php'
        cookieService.getCookieFromUrl(loginUrl, _) >> clientId

        when:
        final def sut = new WebParserIliasServiceProvider(cookieService, loginUrl)

        then:
        sut.clientId == clientId
    }

    def "constructor: prepend 'https://' if loginUrl does not start with it"() {
        setup:
        final def clientId = 'client-id'
        final def loginUrl = 'www.ilias.fh-dortmund.de/ilias/login.php'
        cookieService.getCookieFromUrl(loginUrl, _) >> clientId

        when:
        final def sut = new WebParserIliasServiceProvider(cookieService, loginUrl)

        then:
        sut.clientId == clientId
        sut.iliasBaseUrl == "https://www.ilias.fh-dortmund.de/ilias/"
    }

    @Unroll
    def "constructor: does not prepend 'https://' if loginUrl starts with '#prefix'"() {
        setup:
        final def clientId = 'client-id'
        cookieService.getCookieFromUrl(loginUrl, _) >> clientId

        when:
        final def sut = new WebParserIliasServiceProvider(cookieService, loginUrl)

        then:
        sut.clientId == clientId
        sut.iliasBaseUrl == expectedUrl

        where:
        prefix | loginUrl | expectedUrl
        'https://' | 'https://www.ilias.fh-dortmund.de/ilias/login.php' | 'https://www.ilias.fh-dortmund.de/ilias/'
        'http://' | 'http://www.ilias.fh-dortmund.de/ilias/login.php' | 'http://www.ilias.fh-dortmund.de/ilias/'
    }
}
