package com.github.thetric.iliasdownloader.service.soap;

import com.github.thetric.iliasdownloader.service.IliasService;
import com.github.thetric.iliasdownloader.service.model.Course;
import com.github.thetric.iliasdownloader.service.soap.model.LoginType;
import com.github.thetric.iliasdownloader.service.model.LoginCredentials;
import io.reactivex.Observable;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Collections.singletonList;

/**
 * @author broj
 * @since 21.05.2016
 */
@Log4j2
@RequiredArgsConstructor
public final class SoapIliasService implements IliasService {
    @NonNull
    private final IliasSoapConnector connectorService;
    @NonNull
    private final LoginType loginType;

    private String sessionId;

    /**
     * Loggt sich am Ilias ein.
     *
     * @param loginCredentials
     *         Logindaten
     */
    @Override
    public void login(@NonNull LoginCredentials loginCredentials) {
        final String loginMethodName = loginType.getLoginMethodName();

        final Object authResponse = connectorService.executeSoapRequest(loginMethodName, mapLoginData(loginCredentials));
        sessionId = String.valueOf(authResponse);
    }

    // NOTE: es ist wichtig, in welcher _Reihenfolge_ die Parameter dem SoapObjekt hinzugefügt werden

    private List<SoapParameterEntry> mapLoginData(LoginCredentials loginCredentials) {
        List<SoapParameterEntry> entries = new ArrayList<>();
        entries.add(new SoapParameterEntry("client", connectorService.getClientId()));
        entries.add(new SoapParameterEntry("username", loginCredentials.getUserName()));
        entries.add(new SoapParameterEntry("password", loginCredentials.getPassword()));
        return entries;
    }

    @Override
    public void logout() {
        connectorService.executeSoapRequest("logout", singletonList(new SoapParameterEntry("sid", sessionId)));
    }

    @Override
    public Observable<Course> getJoinedCourses() {
        return Observable.empty();
    }

    @Override
    public Observable<Course> searchCoursesWithContent(Collection<Course> selectedCourses) {
        return Observable.empty();
    }
}
