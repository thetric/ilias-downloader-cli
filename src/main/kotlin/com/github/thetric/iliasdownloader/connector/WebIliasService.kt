package com.github.thetric.iliasdownloader.connector

import com.github.thetric.iliasdownloader.connector.model.Course
import com.github.thetric.iliasdownloader.connector.model.CourseFile
import com.github.thetric.iliasdownloader.connector.model.LoginCredentials
import mu.KotlinLogging
import org.apache.http.HttpStatus
import org.jsoup.Connection
import org.jsoup.Jsoup
import java.io.InputStream

private const val COURSE_SELECTOR = "a[href*='_crs_'].il_ContainerItemTitle"

/**
 * [IliasService] parsing the HTML from the Ilias website.
 */
internal class WebIliasService(
    private val itemParser: IliasItemParser,
    private val courseSyncService: CourseSyncService,
    iliasBaseUrl: String, clientId: String,
    private val courseOverview: String
) : IliasService {
    private val loginPage =
        "${iliasBaseUrl}ilias.php?lang=en&client_id=${clientId}&cmd=post&cmdClass=ilstartupgui&cmdNode=yc" +
            "&baseClass=ilStartUpGUI&rtoken="
    private val logoutPage = "${iliasBaseUrl}logout.php"
    private val log = KotlinLogging.logger {}
    private val cookies = mutableMapOf<String, String>()


    override fun getContentAsStream(file: CourseFile): InputStream {
        return courseSyncService.getContentAsStream(file)
    }

    override fun login(loginCredentials: LoginCredentials) {
        log.info { "Logging in at $loginPage" }
        val response = Jsoup.connect(loginPage)
            .method(Connection.Method.POST)
            .data("username", loginCredentials.userName)
            .data("password", loginCredentials.password)
            // magic string to make the login work
            .data("cmd[doStandardAuthentication]", "Login")
            .followRedirects(true)
            .execute()
        checkResponse(loginPage, response)
        cookies.putAll(response.cookies())
        log.info { "Login at $loginPage succeeded " }
        courseSyncService.login(loginCredentials)
    }

    private fun checkResponse(url: String, response: Connection.Response) {
        if (response.statusCode() != HttpStatus.SC_OK) {
            val msg = "Failed to GET $url: HTTP status code ${response.statusCode()}"
            throw IliasHttpException(msg, url, response.statusCode())
        }
    }

    private fun clearCookies() {
        cookies.clear()
    }

    override fun logout() {
        log.info { "Logging out: $logoutPage" }
        val response = Jsoup.connect(logoutPage).execute()
        clearCookies()
        checkResponse(logoutPage, response)
        log.info { "Logout at ${logoutPage}succeeded" }
    }

    override fun getJoinedCourses(): Collection<Course> {
        log.info { "Get all courses and groups from $courseOverview" }
        val response = Jsoup.connect(courseOverview).cookies(cookies).execute()
        checkResponse(courseOverview, response)
        val document = response.parse()
        return document.select(COURSE_SELECTOR).map { itemParser.parseCourse(it) }
    }

    override fun <C> visit(courseItem: Course, itemListener: IliasItemListener<C>, initialContext: C) {
        courseSyncService.walkIliasCourse(courseItem, itemListener, initialContext)
    }

    override fun close() {
        // nothing to do
    }
}
