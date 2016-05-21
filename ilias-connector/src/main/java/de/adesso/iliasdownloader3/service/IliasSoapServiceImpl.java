package de.adesso.iliasdownloader3.service;

import de.adesso.iliasdownloader3.exception.IliasAuthenticationException;
import de.adesso.iliasdownloader3.exception.IliasException;
import de.adesso.iliasdownloader3.model.SoapParameterEntry;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.log4j.Log4j2;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.xmlpull.v1.XmlPullParserException;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.Objects.requireNonNull;

/**
 * Created by Dominik Broj on 02.02.2016.
 *
 * @author Dominik Broj
 * @since 02.02.2016
 */
@Log4j2
public final class IliasSoapServiceImpl implements IliasSoapService {
    private static final String ILIAS_SOAP_XML_NAMESPACE = "http://schemas.xmlsoap.org/soap/envelope/";

    private final String iliasSoapServerUrl, clientId;

    private final AtomicBoolean webdavEnabled = new AtomicBoolean();
    private String sessionId;

    // NOTE: es ist wichtig, in welcher _Reihenfolge_ die Parameter dem SoapObjekt hinzugefügt werden

    public IliasSoapServiceImpl(@NonNull String iliasSoapServerUrl, @NonNull String clientId) {
        this.iliasSoapServerUrl = iliasSoapServerUrl;
        this.clientId = clientId;
    }

    private static PropertyInfo createPropertyInfo(String key, String value) {
        final PropertyInfo propInfo = new PropertyInfo();
        propInfo.setName(key);
        propInfo.setValue(value);
        propInfo.setType(value.getClass());
        return propInfo;
    }

    private static PropertyInfo createPropertyInfoFromEntry(SoapParameterEntry entry) {
        return createPropertyInfo(entry.getKey(), entry.getValue());
    }

    @Override
    public void login(@NonNull LoginData loginData) throws IOException, XmlPullParserException {
        requireNonNull(loginData.getLoginType(), "loginData.getLoginType()");

        sessionId = authenticate(loginData);
    }

    /**
     * Loggt sich am Ilias ein.
     *
     * @param loginData
     *         Logindaten
     * @return Session ID
     * @throws IliasException
     * @throws IliasHttpsException
     *         falls es Probleme mit der SSL Verbindung gab (eigentlich nur ein Problem unter Java < 1.8)
     */
    private String authenticate(LoginData loginData) throws IliasException, IOException, XmlPullParserException {
        final String loginMethodName = loginData.getLoginType().getLoginMethodName();

        final Object authResponse = executeSoapRequest(loginMethodName, mapLoginData(loginData));
        sessionId = ((String) authResponse);
        return null;
    }

    private Object executeSoapRequest(String soapMethodName, Collection<SoapParameterEntry> parameters) {
        SoapObject soapObject = new SoapObject(ILIAS_SOAP_XML_NAMESPACE, soapMethodName);
        setPropertyInfosFromMap(soapObject, parameters);

        SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        soapEnvelope.setOutputSoapObject(soapObject);

        final HttpTransportSENoUserAgent http = new HttpTransportSENoUserAgent(iliasSoapServerUrl);
        http.setXmlVersionTag("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");

        final String soapAction = String.join("/", iliasSoapServerUrl, soapMethodName);
        try {
            http.call(soapAction, soapEnvelope);
            return soapEnvelope.getResponse();
        } catch (SSLException sslEx) {
            // sollte nur unter Java Version < 1.8 auftreten
            System.err.println("SSL Exception occurred with soap action: " + soapAction);
            throw new IliasHttpsException(sslEx);
        } catch (SoapFault soapEx) {
            System.err.println("Soap fault occurred with soap action: " + soapAction);
            throw new IliasAuthenticationException(soapEx);
        } catch (XmlPullParserException | IOException e) {
            System.err.println("Exception occurred while trying to authenticate with soap action: " + soapAction);
            throw new IliasException(e);
        }
    }

    private void setPropertyInfosFromMap(SoapObject soapObject, Collection<SoapParameterEntry> parameters) {
        parameters.stream().map(IliasSoapServiceImpl::createPropertyInfoFromEntry).forEach(soapObject::addProperty);
    }

    private Collection<SoapParameterEntry> mapLoginData(LoginData loginData) {
        final ArrayList<SoapParameterEntry> entries = new ArrayList<>();
        entries.add(new SoapParameterEntry("client", clientId));
        entries.add(new SoapParameterEntry("username", loginData.getUserName()));
        entries.add(new SoapParameterEntry("password", loginData.getPassword()));
        return entries;
    }

    @Value
    private static final class SoapParameterEntry {
        private final String key, value;
    }

}
