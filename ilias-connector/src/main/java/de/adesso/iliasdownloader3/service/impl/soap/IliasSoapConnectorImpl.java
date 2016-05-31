package de.adesso.iliasdownloader3.service.impl.soap;

import de.adesso.iliasdownloader3.exception.IliasException;
import de.adesso.iliasdownloader3.model.SoapParameterEntry;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;

/**
 * Created by Dominik Broj on 02.02.2016.
 *
 * @author Dominik Broj
 * @since 02.02.2016
 */
@Log4j2
@RequiredArgsConstructor
public final class IliasSoapConnectorImpl implements IliasSoapConnector {
    private static final String ILIAS_SOAP_XML_NAMESPACE = "http://schemas.xmlsoap.org/soap/envelope/";

    @NonNull
    private final String iliasSoapServerUrl;
    @NonNull
    @Getter
    private final String clientId;

    @Override
    public Object executeSoapRequest(String soapMethodName, List<SoapParameterEntry> parameters) {
        SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        soapEnvelope.setOutputSoapObject(createSoapObject(soapMethodName, parameters));

        final HttpTransportSENoUserAgent http = new HttpTransportSENoUserAgent(iliasSoapServerUrl);
        http.setXmlVersionTag("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");

        final String soapAction = String.join("/", iliasSoapServerUrl, soapMethodName);

        try {
            http.call(soapAction, soapEnvelope);
            return soapEnvelope.getResponse();
        } catch (IOException | XmlPullParserException e) {
            throw new IliasException(e);
        }
    }

    private SoapObject createSoapObject(String soapMethodName, List<SoapParameterEntry> parameters) {
        SoapObject soapObject = new SoapObject(ILIAS_SOAP_XML_NAMESPACE, soapMethodName);
        addPropertyInfo(soapObject, parameters);
        return soapObject;
    }

    private void addPropertyInfo(SoapObject soapObject, List<SoapParameterEntry> parameters) {
        parameters.stream().map(this::createPropertyInfo).forEach(soapObject::addProperty);
    }

    private PropertyInfo createPropertyInfo(String key, String value) {
        final PropertyInfo propInfo = new PropertyInfo();
        propInfo.setName(key);
        propInfo.setValue(value);
        propInfo.setType(value.getClass());
        return propInfo;
    }

    private PropertyInfo createPropertyInfo(SoapParameterEntry entry) {
        return createPropertyInfo(entry.getKey(), entry.getValue());
    }
}
