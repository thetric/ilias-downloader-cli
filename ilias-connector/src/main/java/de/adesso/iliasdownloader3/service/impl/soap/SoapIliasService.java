package de.adesso.iliasdownloader3.service.impl.soap;

import de.adesso.iliasdownloader3.model.LoginCredentials;
import de.adesso.iliasdownloader3.model.LoginType;
import de.adesso.iliasdownloader3.model.SoapParameterEntry;
import de.adesso.iliasdownloader3.service.IliasService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
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

    private String sessionId;

    // NOTE: es ist wichtig, in welcher _Reihenfolge_ die Parameter dem SoapObjekt hinzugefügt werden

    /**
     * Loggt sich am Ilias ein.
     *
     * @param loginCredentials
     *         Logindaten
     */
    @Override
    public void login(@NonNull LoginCredentials loginCredentials) {
        final String loginMethodName = loginCredentials.getLoginType().getLoginMethodName();

        final Object authResponse = connectorService.executeSoapRequest(loginMethodName, mapLoginData(loginCredentials));
        sessionId = String.valueOf(authResponse);
    }

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

    public static void main(String[] args) {
        String url = "https://www.ilias.fh-dortmund.de/ilias/login.php?client_id=ilias-fhdo&lang=de";
        IliasSoapConnectorImpl soapService = new IliasSoapConnectorImpl(url, "ilias-fhdo");
        // TODO testen
        new SoapIliasService(soapService).login(new LoginCredentials("dobro001", "n8=QdVjr", LoginType.CAS, null));
    }
}
