package de.adesso.iliasdownloader3.service;

import de.adesso.iliasdownloader3.model.SoapParameterEntry;

import java.io.IOException;

/**
 * Created by Dominik Broj on 02.02.2016.
 *
 * @author Dominik Broj
 * @since 02.02.2016
 */
public interface IliasSoapService {
    Object executeSoapRequest(String soapMethodName, List<SoapParameterEntry> parameters);

    String getClientId();
}
