package com.github.thetric.iliasdownloader.service.soap

import com.github.thetric.iliasdownloader.service.exception.IliasException
import groovy.transform.CompileStatic
import groovy.transform.Immutable
import org.ksoap2.SoapEnvelope
import org.ksoap2.serialization.PropertyInfo
import org.ksoap2.serialization.SoapObject
import org.ksoap2.serialization.SoapSerializationEnvelope
import org.ksoap2.transport.HttpTransportSE
import org.xmlpull.v1.XmlPullParserException

/**
 * Created by Dominik Broj on 02.02.2016.
 *
 * @author Dominik Broj
 * @since 02.02.2016
 */
@CompileStatic
@Immutable
final class IliasSoapConnectorImpl implements IliasSoapConnector {
    private static final String ILIAS_SOAP_XML_NAMESPACE = "http://schemas.xmlsoap.org/soap/envelope/"

    private final String iliasSoapServerUrl
    final String clientId

    @Override
    Object executeSoapRequest(String soapMethodName, List<SoapParameterEntry> parameters) {
        SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11)
        soapEnvelope.outputSoapObject = createSoapObject(soapMethodName, parameters)

        final HttpTransportSE http = new HttpTransportSE(iliasSoapServerUrl)
        http.xmlVersionTag = '<?xml version="1.0" encoding="UTF-8"?>'

        final String soapAction = "$iliasSoapServerUrl/$soapMethodName"

        try {
            http.call(soapAction, soapEnvelope)
            return soapEnvelope.response
        } catch (IOException | XmlPullParserException e) {
            throw new IliasException(e)
        }
    }

    private SoapObject createSoapObject(String soapMethodName, List<SoapParameterEntry> parameters) {
        SoapObject soapObject = new SoapObject(ILIAS_SOAP_XML_NAMESPACE, soapMethodName)
        addPropertyInfo(soapObject, parameters)
        return soapObject
    }

    private void addPropertyInfo(SoapObject soapObject, List<SoapParameterEntry> parameters) {
        parameters.stream().map this.&createPropertyInfo each soapObject.&addProperty
    }

    private PropertyInfo createPropertyInfo(String key, String value) {
        final PropertyInfo propInfo = new PropertyInfo()
        propInfo.name = key
        propInfo.value = value
        propInfo.type = value.class
        return propInfo
    }

    private PropertyInfo createPropertyInfo(SoapParameterEntry entry) {
        return createPropertyInfo(entry.key, entry.value)
    }
}
