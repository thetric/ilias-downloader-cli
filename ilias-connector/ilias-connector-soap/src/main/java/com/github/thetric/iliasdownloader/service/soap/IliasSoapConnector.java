package com.github.thetric.iliasdownloader.service.soap;

import java.util.List;

/**
 * Created by Dominik Broj on 02.02.2016.
 *
 * @author Dominik Broj
 * @since 02.02.2016
 */
public interface IliasSoapConnector {
    Object executeSoapRequest(String soapMethodName, List<SoapParameterEntry> parameters);

    String getClientId();
}
