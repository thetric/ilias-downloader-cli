package de.adesso.iliasdownloader2.service;

import de.adesso.iliasdownloader2.util.TwoObjectsX;
import de.adesso.iliasdownloader3.exception.IliasAuthenticationException;
import de.adesso.iliasdownloader3.exception.IliasException;
import de.adesso.iliasdownloader3.exception.IliasHttpsException;
import de.adesso.iliasdownloader3.service.HttpTransportSENoUserAgent;
import de.adesso.iliasdownloader3.service.LoginData;
import de.adesso.iliasdownloader3.xmlentities.course.XmlCourse;
import de.adesso.iliasdownloader3.xmlentities.exercise.XmlExercise;
import de.adesso.iliasdownloader3.xmlentities.file.XmlFileContent;
import de.adesso.iliasdownloader3.xmlentities.filetree.XmlObject;
import de.adesso.iliasdownloader3.xmlentities.filetree.XmlObjects;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.xmlpull.v1.XmlPullParserException;

import javax.net.ssl.SSLException;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import static de.adesso.iliasdownloader2.util.Functions.parseXmlObject;

/**
 * @author krummenauer
 */
@Deprecated
public final class ILIASSoapService {
	@Getter
	private String soapServerURL;

	@Getter
	private String clientName;

	@Getter
	@Setter
	private String sessionId = null;

	@Getter
	private boolean webdavAuthenticationActive = false;

	private long userId = -1;

	public ILIASSoapService(String soapServerURL, String clientName) {
		this.soapServerURL = soapServerURL;
		this.clientName = clientName;
	}

	public String getIliasInstallationURL() {
		return IliasUtil.getIliasInstallationURL(soapServerURL);
	}

	public String getURLInIlias(XmlObject xmlObject) {
		return getIliasInstallationURL() + "/goto.php?target=" + xmlObject.getType() + "_" + xmlObject.getRefIdOne();
	}

	public InputStream getWebdavFileStream(long refId) {
		try {
			return new BufferedInputStream(new URL(IliasUtil.findWebdavByWebservice(soapServerURL) + "/" + clientName + "/ref_" + refId).openConnection().getInputStream());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param loginData
	 *
	 * @throws IliasAuthenticationException
	 */
	public void login(LoginData loginData) {
		webdavAuthenticationActive = false;
		String methodName;
		userId = -1;

		switch (loginData.getLoginType()) {
			case DEFAULT:
				methodName = "login";
				break;
			case LDAP:
				methodName = "loginLDAP";
				break;
			case CAS:
				methodName = "loginCAS";
				break;
			default:
				throw new IliasAuthenticationException("loginType was " + loginData.getLoginType());
		}

		SoapResult soapResult;
		try {
			soapResult = sendSoapRequestGetSoapBody(methodName, new TwoObjectsX<>("client", clientName),
					new TwoObjectsX<>("username", loginData.getUserName()), new TwoObjectsX<>("password", loginData.getPassword()));
		} catch (IliasHttpsException e) {
			throw e;
		} catch (Exception e) {
			throw new IliasAuthenticationException("Error during login. Maybe wrong Server or wrong client id (not username)", e);
		}
		sessionId = soapResult.getText();
		String error = soapResult.getError();

		if (error != null && !error.trim().isEmpty()) {
			sessionId = null;

			if (error.equals("Authentication failed.")) {
				throw new IliasAuthenticationException("Authentication failed. Wrong username/password");
			}
		} /*else {
			if (loginData.isWebDavAuthenticationEnabled()) {
				enableWebdavAuthentication(loginData.getUserName(), loginData.getPassword().toCharArray());
			}
		}*/

	}


	public void enableWebdavAuthentication(final String username, final char[] password) {
		webdavAuthenticationActive = true;


	}

	public boolean logout() {
		userId = -1;
		boolean result = Boolean.parseBoolean(sendSoapRequest("logout", new TwoObjectsX<>("sid", sessionId)));
		//		try {
		sessionId = null;
		//			soapConnection.close();
		//		} catch (SOAPException e) {
		//			throw new IliasException(e);
		//		}
		//
		return result;
	}

	public boolean isLoggedIn() {
		if (sessionId == null) {
			return false;
		}

		SoapResult soapResult;
		try {
			soapResult = sendSoapRequestGetSoapBody("getUserIdBySid", new TwoObjectsX<>("sid", sessionId));
		} catch (Exception e) {
			return false;
		}

		return !soapResult.isFaultCode();
	}

	public XmlExercise getExercise(long refId) {
		String s = sendSoapRequest("getExerciseXML", new TwoObjectsX<>("sid", sessionId), new TwoObjectsX<>("ref_id", refId), new TwoObjectsX<>("attachment_mode", 1));
		return parseXmlObject(s, XmlExercise.class);
	}

	public XmlFileContent getFile(long refId) {
		String s = sendSoapRequest("getFileXML", new TwoObjectsX<>("sid", sessionId), new TwoObjectsX<>("ref_id", refId), new TwoObjectsX<>("attachment_mode", 1));

		return parseXmlObject(s, XmlFileContent.class);
	}

	public List<XmlObject> getCourseObjects(long refId) {
		return getCourseObjects(refId, getUserId());
	}

	public List<XmlObject> getCourseObjects(long refId, long userId) {
		String s = sendSoapRequest("getXMLTree", new TwoObjectsX<>("sid", sessionId), new TwoObjectsX<>("ref_id", refId), new TwoObjectsX<>("types", ""), new TwoObjectsX<>("user_id", userId));
		return parseXmlObject(s, XmlObjects.class).getObjects();

	}

	public XmlCourse getCourse(long refId) {
		String s = sendSoapRequest("getCourseXML", new TwoObjectsX<>("sid", sessionId), new TwoObjectsX<>("course_id", refId));
		if (s == null) {
			throw new IliasException("Course with RefId " + refId + " not accessible. Are you still in that course?");
		}

		return parseXmlObject(s, XmlCourse.class);
	}

	public long getRefIdByObjId(long objId) {
		return Long.parseLong(sendSoapRequest("getRefIdsByObjId", new TwoObjectsX<>("sid", sessionId), new TwoObjectsX<>("obj_id", objId)));
	}

	public long getUserId() {
		return userId != -1 ? userId : (userId = Long.parseLong(sendSoapRequest("getUserIdBySid", new TwoObjectsX<>("sid", sessionId))));
	}

	public List<Long> getCourseIds() {
		return getCourseIds(getUserId());
	}

	public List<Long> getCourseIds(long userId) {
		val result = new LinkedList<Long>();

		String s = sendSoapRequest("getUserRoles", new TwoObjectsX<>("sid", sessionId), new TwoObjectsX<>("user_id", userId));
		XmlObjects objects = parseXmlObject(s, XmlObjects.class);

		final String ID_PREFIX = "il_crs_member_";
		objects.getObjects().stream().filter(x -> x.getTitle().contains(ID_PREFIX)).forEach(x -> {
			Long id = Long.parseLong(x.getTitle().substring(ID_PREFIX.length()));
			if (id != null) {
				result.add(id);
			}
		});

		return result;
	}

	private String sendSoapRequest(String soapMethodName, TwoObjectsX<?, ?>... mapNameToValue) {
		return sendSoapRequestGetSoapBody(soapMethodName, mapNameToValue).getText();
	}

	private SoapResult sendSoapRequestGetSoapBody(String soapMethodName, TwoObjectsX<?, ?>... mapNameToValue) {
		SoapObject soapObject = new SoapObject("http://schemas.xmlsoap.org/soap/envelope/", soapMethodName);

		for (val nameToValue : mapNameToValue) {
			PropertyInfo propertyInfo = new PropertyInfo();
			propertyInfo.setName(String.valueOf(nameToValue.getObjectA()));
			propertyInfo.setValue(String.valueOf(nameToValue.getObjectB()));
			propertyInfo.setType(nameToValue.getObjectB().getClass());
			soapObject.addProperty(propertyInfo);
		}

		SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		soapEnvelope.setOutputSoapObject(soapObject);

		SoapResult result = new SoapResult();
		result.setFaultCode(false);

		HttpTransportSENoUserAgent http = new HttpTransportSENoUserAgent(soapServerURL);
		http.setXmlVersionTag("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		try {
			http.call(soapServerURL + "/" + soapMethodName, soapEnvelope);

			result.setText(String.valueOf(soapEnvelope.getResponse()));
		} catch (SSLException e) {
			IliasUtil.throwDefaultHTTPSException(e);

		} catch (SoapFault soapFault) {
			result.setFaultCode(true);
			result.setError(soapFault.getMessage());

		} catch (IOException | XmlPullParserException e) {
			throw new RuntimeException(e);
		}

		return result;
	}
}
