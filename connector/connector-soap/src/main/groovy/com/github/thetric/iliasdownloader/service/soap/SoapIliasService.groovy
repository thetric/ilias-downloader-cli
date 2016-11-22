package com.github.thetric.iliasdownloader.service.soap

import com.github.thetric.iliasdownloader.service.IliasService
import com.github.thetric.iliasdownloader.service.model.Course
import com.github.thetric.iliasdownloader.service.model.CourseFile
import com.github.thetric.iliasdownloader.service.model.LoginCredentials
import com.github.thetric.iliasdownloader.service.soap.model.LoginType
import groovy.transform.CompileStatic
import groovy.util.logging.Log4j2
import io.reactivex.Observable
import io.reactivex.Single

import static java.util.Collections.singletonList

/**
 * @author broj
 * @since 21.05.2016
 */
@Log4j2
@CompileStatic
final class SoapIliasService implements IliasService {
    private final IliasSoapConnector connectorService
    private final LoginType loginType

    private String sessionId

    SoapIliasService(IliasSoapConnector connectorService, LoginType loginType) {
        this.connectorService = connectorService
        this.loginType = loginType
    }

    /**
     * Loggt sich am Ilias ein.
     *
     * @param loginCredentials
     *         Logindaten
     */
    @Override
    void login(LoginCredentials loginCredentials) {
        final String loginMethodName = loginType.getLoginMethodName()

        final Object authResponse = connectorService.executeSoapRequest(loginMethodName, mapLoginData(loginCredentials))
        sessionId = String.valueOf(authResponse)
    }

    // NOTE: es ist wichtig, in welcher _Reihenfolge_ die Parameter dem SoapObjekt hinzugefügt werden

    private List<SoapParameterEntry> mapLoginData(LoginCredentials loginCredentials) {
        List<SoapParameterEntry> entries = new ArrayList<>()
        entries.add(new SoapParameterEntry("client", connectorService.getClientId()))
        entries.add(new SoapParameterEntry("username", loginCredentials.getUserName()))
        entries.add(new SoapParameterEntry("password", loginCredentials.getPassword()))
        return entries
    }

    @Override
    void logout() {
        connectorService.executeSoapRequest("logout", singletonList(new SoapParameterEntry("sid", sessionId)))
    }

    @Override
    Observable<Course> getJoinedCourses() {
        return Observable.empty()
    }

    @Override
    Observable<Course> searchCoursesWithContent(Collection<Course> selectedCourses) {
        return Observable.empty()
    }

    @Override
    Single<byte[]> getContent(CourseFile file) {
        return Single.never()
    }
}
