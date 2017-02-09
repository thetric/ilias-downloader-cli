package com.github.thetric.iliasdownloader.service.webparser.impl

import com.github.thetric.iliasdownloader.service.IliasService
import com.github.thetric.iliasdownloader.service.exception.IliasAuthenticationException
import com.github.thetric.iliasdownloader.service.model.Course
import com.github.thetric.iliasdownloader.service.model.CourseFile
import com.github.thetric.iliasdownloader.service.model.CourseItem
import com.github.thetric.iliasdownloader.service.model.LoginCredentials
import com.github.thetric.iliasdownloader.service.webparser.impl.course.CourseSyncService
import com.github.thetric.iliasdownloader.service.webparser.impl.util.WebIoExceptionTranslator
import com.github.thetric.iliasdownloader.service.webparser.impl.util.fluenthc.FluentHcExecutorProvider
import groovy.transform.CompileStatic
import groovy.util.logging.Log4j2
import org.apache.http.client.fluent.Executor
import org.apache.http.client.fluent.Form
import org.apache.http.client.fluent.Request
import org.apache.http.impl.client.BasicCookieStore

import java.util.function.Supplier

/**
 * @author broj
 * @since 31.05.2016
 */
@Log4j2
@CompileStatic
final class WebIliasService implements IliasService {
    private final WebIoExceptionTranslator exceptionTranslator
    private final FluentHcExecutorProvider fluentHcExecutorProvider
    private final Supplier<? extends CourseSyncService> courseSyncServiceSupplier

    private final String iliasBaseUrl
    private final String loginPage
    private final String logoutPage
    private final String clientId

    // per default Groovy imports java.net.* where a CookieStore interface already exists
    // so we must use the full qualified import ;(
    private final org.apache.http.client.CookieStore cookieStore

    WebIliasService(
        WebIoExceptionTranslator exceptionTranslator,
        String iliasBaseUrl, String clientId,
        FluentHcExecutorProvider fluentHcExecutorProvider,
        Supplier<? extends CourseSyncService> courseSyncServiceSupplier) {
        this.exceptionTranslator = exceptionTranslator
        this.iliasBaseUrl = iliasBaseUrl
        this.clientId = clientId
        loginPage = "${iliasBaseUrl}login.php"
        logoutPage = "${iliasBaseUrl}logout.php"

        this.fluentHcExecutorProvider = fluentHcExecutorProvider
        cookieStore = new BasicCookieStore()

        this.courseSyncServiceSupplier = courseSyncServiceSupplier
    }

    @Override
    InputStream getContentAsStream(CourseFile courseFile) {
        return connectWithSessionCookies().execute(Request.Get(courseFile.url))
                                          .returnContent()
                                          .asStream()
    }

    @Override
    void login(LoginCredentials loginCredentials) {
        log.info('Logging in at {}', loginPage)
        try {
            connectWithSessionCookies().execute(Request.Post(loginPage)
                                                       .bodyForm(Form.form()
                                                                     .add('username', loginCredentials.userName)
                                                                     .add('password', loginCredentials.password)
                                                                     .build()))
                                       .discardContent()
            if (!hasLoginCookie()) {
                cookieStore.clear()
                throw new IliasAuthenticationException('Ungültiger Login')
            }
        } catch (IOException e) {
            log.error("Login at $loginPage failed", e)
            cookieStore.clear()
            throw exceptionTranslator.translate(e)
        }
        log.info('Login at {} succeeded', loginPage)
    }

    private boolean hasLoginCookie() {
        return cookieStore.getCookies()
                          .any({ it.name == 'authchallenge' })
    }

    @Override
    void logout() {
        log.info('Logging out: {}', logoutPage)

        try {
            connectWithSessionCookies().execute(Request.Get(logoutPage))
                                       .discardContent()
        } catch (IOException e) {
            log.error("Logout at $logoutPage failed", e)
            throw exceptionTranslator.translate(e)
        } finally {
            cookieStore.clear()
        }
        log.info('Logout at {} succeeded', logoutPage)
    }

    private CourseSyncService getCourseSyncService() { courseSyncServiceSupplier.get() }

    private Executor connectWithSessionCookies() {
        return fluentHcExecutorProvider.createFluentHcExecutor(cookieStore)
    }

    @Override
    Collection<Course> getJoinedCourses() {
        return courseSyncService.getJoinedCourses(connectWithSessionCookies())
    }

    @Override
    Collection<? extends CourseItem> getCourseItems(Course course) {
        return courseSyncService.searchAllItems(course, connectWithSessionCookies())
    }

}
