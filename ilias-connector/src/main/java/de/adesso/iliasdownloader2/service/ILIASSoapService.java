package de.adesso.iliasdownloader2.service;

import de.adesso.iliasdownloader2.exception.IliasAuthenticationException;
import de.adesso.iliasdownloader2.exception.IliasException;
import de.adesso.iliasdownloader2.exception.IliasHTTPSException;
import de.adesso.iliasdownloader2.util.LoginType;
import de.adesso.iliasdownloader2.util.SOAPResult;
import de.adesso.iliasdownloader2.util.TwoObjectsX;
import de.adesso.iliasdownloader2.xmlentities.course.XmlCourse;
import de.adesso.iliasdownloader2.xmlentities.exercise.XmlExercise;
import de.adesso.iliasdownloader2.xmlentities.file.XmlFileContent;
import de.adesso.iliasdownloader2.xmlentities.filetree.XmlObject;
import de.adesso.iliasdownloader2.xmlentities.filetree.XmlObjects;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import javax.net.ssl.SSLException;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import static de.adesso.iliasdownloader2.util.Functions.parseXmlObject;

//import javax.xml.soap.MessageFactory;
//import javax.xml.soap.MimeHeaders;
//import javax.xml.soap.SOAPBody;
//import javax.xml.soap.SOAPConnection;
//import javax.xml.soap.SOAPConnectionFactory;
//import javax.xml.soap.SOAPElement;
//import javax.xml.soap.SOAPEnvelope;
//import javax.xml.soap.SOAPException;
//import javax.xml.soap.SOAPMessage;
//import javax.xml.soap.SOAPPart;

public class ILIASSoapService {

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

	//	private SOAPConnection soapConnection;

	//	public static void main(String[] args) throws Exception {
	//		ILIASSoapService iliasSoapService = new ILIASSoapService("https://www.ilias.fh-dortmund.de/ilias/webservice/soap/server.php", "ilias-fhdo");
	//		//		String s = iliasSoapService.sendSOAPMessage("getRefIdsByObjId", asMap(String.class, Object.class, new Object[][]{{"sid", }, {"obj_id", 492502}}));
	//
	//		//		iliasSoapService.login("kekru001", JOptionPane.showInputDialog("Passwort"), LoginType.LDAP);
	//		iliasSoapService.setSessionId("vdqpje385935ci9a4orbg35mc1::ilias-fhdo");
	//
	////		val stream = iliasSoapService.getFileStream(361054);
	////		val out = new FileOutputStream(new File("D:/hallo.pdf"));
	////		IOUtils.copy(stream, out);
	//
	//		System.out.println(iliasSoapService.getSessionId());
	//		System.out.println(iliasSoapService.getRefIdByObjId(497091));
	//		long userId = iliasSoapService.getUserId();
	//		System.out.println("UserID: " + userId);
	//
	//		//		System.out.println("kurs ids: " + iliasSoapService.getCourseIds2(userId));
	//
	//		System.out.println(iliasSoapService.getCourse(338991L));
	//
	//		List<Long> courseIds = iliasSoapService.getCourseIds(userId);
	//		courseIds.add(338991L);
	//		System.out.println("KursIDs: " + courseIds);
	//		//		System.out.println(iliasSoapService.getCourse2(2612));
	//
	//
	//
	//		//		for(val id : courseIds){
	//		//			List<XmlObject> folderTree = iliasSoapService.getFolderTree(id, userId);
	//		//			System.out.println(folderTree);
	//		//		}
	//
	//				FileSync fileSync = new FileSync("D:/ilias");
	//				fileSync.addToIgnore("D:\\ilias\\Mobile Sicherheit\\Demo\\? mim wlan02 - YouTube [720p].mp4");
	//				fileSync.addToIgnore("D:\\ilias\\Seminar Inhalt Startup Wettbewerb Gestaltung eines realen e-Handelsplatzes\\Give me 50 - Kopie.pptx");
	//				fileSync.addToIgnore("D:\\ilias\\Componentware\\Praktikum � Phase 1\\00 Installation\\glassfish-4.0-ml.zip");
	//				fileSync.syncFiles(iliasSoapService);
	//
	//		//		System.out.println(JAXBContext.newInstance(de.adesso.iliasdownloader2.util.XmlObjects.class).createUnmarshaller().unmarshal(ILIASSoapService.class.getResourceAsStream("/de/adesso/iliasdownloader2/service/xml.xml")));
	//
	//		//		System.out.println(new XmlMapper().readValue("<?xml version=\"1.0\" encoding=\"utf-8\"?><!DOCTYPE Objects PUBLIC \"-//ILIAS//DTD ILIAS Repositoryobjects//EN\" \"http://www.ilias.fh-dortmund.de/ilias/xml/ilias_object_4_0.dtd\"><!--Export of ILIAS objects--><Objects><Object type=\"exc\" obj_id=\"480647\"><Title>Componentware: Abgaben</Title><Description/><Owner>36323</Owner><CreateDate>2014-11-01 20:59:18</CreateDate><LastUpdate>2014-11-09 23:58:46</LastUpdate><ImportId/><References ref_id=\"347704\" parent_id=\"23561\" accessInfo=\"granted\"><TimeTarget type=\"0\"><Timing starting_time=\"1414872094\" ending_time=\"1414872094\" visibility=\"0\"/><Suggestion starting_time=\"1414872094\" ending_time=\"1414872094\" changeable=\"0\" earliest_start=\"1414872094\" latest_end=\"1414882500\"/></TimeTarget><Operation>visible</Operation><Operation>read</Operation><Path><Element ref_id=\"1\" type=\"root\">Magazin</Element><Element ref_id=\"34\" type=\"cat\">FB Informatik</Element><Element ref_id=\"37365\" type=\"cat\">Lehrveranstaltungen</Element><Element ref_id=\"10832\" type=\"cat\">Studiengangs�bergreifende Veranstaltungen</Element><Element ref_id=\"23561\" type=\"crs\">Componentware</Element></Path></References></Object><Object type=\"fold\" obj_id=\"457424\"><Title>Praktikum � Phase 1</Title><Description/><Owner>36323</Owner><CreateDate>2014-09-14 23:48:30</CreateDate><LastUpdate>2014-09-14 23:48:30</LastUpdate><ImportId/><References ref_id=\"329607\" parent_id=\"23561\" accessInfo=\"granted\"><TimeTarget type=\"0\"><Timing starting_time=\"1410731352\" ending_time=\"1410731352\" visibility=\"0\"/><Suggestion starting_time=\"1410731352\" ending_time=\"1410731352\" changeable=\"0\" earliest_start=\"1410731352\" latest_end=\"1410731700\"/></TimeTarget><Operation>visible</Operation><Operation>read</Operation><Path><Element ref_id=\"1\" type=\"root\">Magazin</Element><Element ref_id=\"34\" type=\"cat\">FB Informatik</Element><Element ref_id=\"37365\" type=\"cat\">Lehrveranstaltungen</Element><Element ref_id=\"10832\" type=\"cat\">Studiengangs�bergreifende Veranstaltungen</Element><Element ref_id=\"23561\" type=\"crs\">Componentware</Element></Path></References></Object><Object type=\"fold\" obj_id=\"457425\"><Title>Praktikum � Phase 2</Title><Description/><Owner>36323</Owner><CreateDate>2014-09-14 23:48:42</CreateDate><LastUpdate>2014-09-14 23:48:42</LastUpdate><ImportId/><References ref_id=\"329608\" parent_id=\"23561\" accessInfo=\"granted\"><TimeTarget type=\"0\"><Timing starting_time=\"1410731364\" ending_time=\"1410731364\" visibility=\"0\"/><Suggestion starting_time=\"1410731364\" ending_time=\"1410731364\" changeable=\"0\" earliest_start=\"1410731364\" latest_end=\"1410731700\"/></TimeTarget><Operation>visible</Operation><Operation>read</Operation><Path><Element ref_id=\"1\" type=\"root\">Magazin</Element><Element ref_id=\"34\" type=\"cat\">FB Informatik</Element><Element ref_id=\"37365\" type=\"cat\">Lehrveranstaltungen</Element><Element ref_id=\"10832\" type=\"cat\">Studiengangs�bergreifende Veranstaltungen</Element><Element ref_id=\"23561\" type=\"crs\">Componentware</Element></Path></References></Object><Object type=\"fold\" obj_id=\"457426\"><Title>Test und Noten</Title><Description/><Owner>36323</Owner><CreateDate>2014-09-14 23:49:26</CreateDate><LastUpdate>2014-09-14 23:49:26</LastUpdate><ImportId/><References ref_id=\"329609\" parent_id=\"23561\" accessInfo=\"granted\"><TimeTarget type=\"0\"><Timing starting_time=\"1410731408\" ending_time=\"1410731408\" visibility=\"0\"/><Suggestion starting_time=\"1410731408\" ending_time=\"1410731408\" changeable=\"0\" earliest_start=\"1410731408\" latest_end=\"1410731700\"/></TimeTarget><Operation>visible</Operation><Operation>read</Operation><Path><Element ref_id=\"1\" type=\"root\">Magazin</Element><Element ref_id=\"34\" type=\"cat\">FB Informatik</Element><Element ref_id=\"37365\" type=\"cat\">Lehrveranstaltungen</Element><Element ref_id=\"10832\" type=\"cat\">Studiengangs�bergreifende Veranstaltungen</Element><Element ref_id=\"23561\" type=\"crs\">Componentware</Element></Path></References></Object><Object type=\"fold\" obj_id=\"457423\"><Title>Vorlesung</Title><Description/><Owner>36323</Owner><CreateDate>2014-09-14 23:48:17</CreateDate><LastUpdate>2014-09-14 23:48:17</LastUpdate><ImportId/><References ref_id=\"329606\" parent_id=\"23561\" accessInfo=\"granted\"><TimeTarget type=\"0\"><Timing starting_time=\"1410731341\" ending_time=\"1410731341\" visibility=\"0\"/><Suggestion starting_time=\"1410731341\" ending_time=\"1410731341\" changeable=\"0\" earliest_start=\"1410731341\" latest_end=\"1410731700\"/></TimeTarget><Operation>visible</Operation><Operation>read</Operation><Path><Element ref_id=\"1\" type=\"root\">Magazin</Element><Element ref_id=\"34\" type=\"cat\">FB Informatik</Element><Element ref_id=\"37365\" type=\"cat\">Lehrveranstaltungen</Element><Element ref_id=\"10832\" type=\"cat\">Studiengangs�bergreifende Veranstaltungen</Element><Element ref_id=\"23561\" type=\"crs\">Componentware</Element></Path></References></Object></Objects>", de.adesso.iliasdownloader2.util.Objects.class));
	//
	//		//		System.out.println(iliasSoapService.logout());
	//
	//	}

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
	 * @param username
	 * @param password
	 * @param loginType
	 *
	 * @throws IliasAuthenticationException
	 */
	public void login(final String username, final String password, LoginType loginType, final boolean enableWebdavAuthentication) {
		//		try {
		//			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
		//			soapConnection = soapConnectionFactory.createConnection();
		//		} catch (SOAPException e) {
		//			throw new IliasException(e);
		//		}

		webdavAuthenticationActive = false;
		String methodName;
		userId = -1;

		switch (loginType) {
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
				throw new IliasAuthenticationException("loginType was " + loginType);
		}

		//		sessionId = sendSOAPMessageGetValueByPattern(methodName, "<sid xsi:type=\"xsd:string\">.*</sid>", "<[^>]+>", new TwoObjects("client", clientName), new TwoObjects("username", username), new TwoObjects("password", password));
		//		Node firstChild = null;
		SOAPResult soapResult = null;
		try {
			soapResult = sendSoapRequestGetSoapBody(methodName, new TwoObjectsX<>("client", clientName), new TwoObjectsX<>("username", username), new TwoObjectsX<>("password", password));
		} catch (IliasHTTPSException e) {
			throw e;
		} catch (Exception e) {
			throw new IliasAuthenticationException("Error during login. Maybe wrong Server or wrong client id (not username)", e);
		}
		sessionId = soapResult.getText();// firstChild.getFirstChild().getTextContent();
		String error = soapResult.getError();// firstChild.getChildNodes().getLength() >= 2 ? firstChild.getChildNodes().item(1).getTextContent() : null;

		if (error != null && !error.trim().isEmpty()) {
			sessionId = null;

			if (error.equals("Authentication failed.")) {
				throw new IliasAuthenticationException("Authentication failed. Wrong username/password");
			}
		} else {
			if (enableWebdavAuthentication) {
				enableWebdavAuthentication(username, password.toCharArray());
			}
		}

	}


	public void enableWebdavAuthentication(final String username, final char[] password) {
		webdavAuthenticationActive = true;

		Authenticator.setDefault(new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				try {
					if (webdavAuthenticationActive && getRequestingURL().getHost().equals(new URL(soapServerURL).getHost())) {
						return new PasswordAuthentication(username, password);
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
				return null;
			}
		});
	}

	public boolean logout() {
		//		return Boolean.parseBoolean(sendSOAPMessageGetValueByPattern("logout", "<success xsi:type=\"xsd:boolean\">.*</success>", "<[^>]+>", new TwoObjects("sid", sessionId)));
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

		SOAPResult soapResult = null;
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
		//		return new Base64InputStream(new ByteArrayInputStream(fileContent.getContent().getBytes()));


		//		try {
		//			val c = new CircularStream();
		//			Base64.decodeToStream(s, c.getOutputStream()); 
		//			c.getOutputStream().close();
		//			return c.getInputStream();
		//		} catch (IOException e) {
		//			throw new IliasException(e);
		//		}
	}

	public List<XmlObject> getCourseObjects(long refId) {
		return getCourseObjects(refId, getUserId());
	}

	public List<XmlObject> getCourseObjects(long refId, long userId) {
		String s = sendSoapRequest("getXMLTree", new TwoObjectsX<>("sid", sessionId), new TwoObjectsX<>("ref_id", refId), new TwoObjectsX<>("types", ""), new TwoObjectsX<>("user_id", userId));
		return parseXmlObject(s, XmlObjects.class).getObjects();

	}

	//	public List<XmlObject> getFolder(long refId, long userId){
	//		String s = sendSOAPMessageGetValueByPattern("getTreeChilds", "<object_xml xsi:type=\"xsd:string\">.*</object_xml>", "<[^>]+>", new TwoObjects("sid", sessionId), new TwoObjects("ref_id", refId), new TwoObjects("types", ""), new TwoObjects("user_id", userId));
	//		s = s.replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&quot;", "\"");
	//		try {
	//			val c = new CircularStream();
	//			val bw = new BufferedWriter(new OutputStreamWriter(c.getOutputStream()));
	//			bw.write(s);
	//			bw.close();
	//			return ((XmlObjects) JAXBContext.newInstance(de.adesso.iliasdownloader2.xmlentities.filetree.XmlObjects.class).createUnmarshaller().unmarshal(c.getInputStream())).getObjects();
	//		} catch (Exception e) {
	//			throw new IliasException(e);
	//		}
	//	}

	//	public XmlCourse getCourse2(long refId){
	//		try {
	//			final InputStream in = sendSoapRequest("getCourseXML", new TwoObjects("sid", sessionId), new TwoObjects("course_id", refId));
	//
	//			return (XmlCourse) JAXBContext.newInstance(XmlCourse.class).createUnmarshaller().unmarshal(in);
	//		} catch (Exception e) {
	//			throw new IliasException(e);
	//		}
	//	}
	public XmlCourse getCourse(long refId) {
		String s = sendSoapRequest("getCourseXML", new TwoObjectsX<>("sid", sessionId), new TwoObjectsX<>("course_id", refId));
		//		System.out.println(sendSOAPMessage("getCourseXML", new TwoObjects("sid", sessionId), new TwoObjects("course_id", refId)));
		//		s = s.replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&quot;", "\"");
		//		try {
		//			val c = new CircularByteBuffer(CircularByteBuffer.INFINITE_SIZE);
		//			val bw = new BufferedWriter(new OutputStreamWriter(c.getOutputStream()));
		//			bw.write(s);
		//			bw.close();
		//		} catch (Exception e) {
		//			throw new IliasException(e);
		//		}
		if (s == null) {
			throw new IliasException("Course with RefId " + refId + " not accessible. Are you still in that course?");
		}

		return parseXmlObject(s, XmlCourse.class);
	}


	//	public List<XmlObject> getFolderTree(long refId, long userId){
	//		val folder = getFolder(refId, userId);
	//		if(folder != null){
	//			for(val fileOrSubfolder : folder){
	//				if(fileOrSubfolder.isFolder()){
	//					fileOrSubfolder.setChildren(getFolderTree(fileOrSubfolder.getRefId(), userId));
	//				}
	//			}
	//		}
	//		return folder;
	//	}

	public long getRefIdByObjId(long objId) {
		//		return Long.parseLong(sendSOAPMessageGetValueByPattern("getRefIdsByObjId", "<item xsi:type=\"xsd:int\">\\d*</item>", "\\D*", new TwoObjects("sid", sessionId), new TwoObjects("obj_id", objId)));
		return Long.parseLong(sendSoapRequest("getRefIdsByObjId", new TwoObjectsX<>("sid", sessionId), new TwoObjectsX<>("obj_id", objId)));
	}

	public long getUserId() {
		//		return Long.parseLong(sendSoapRequest("getUserIdBySid", "<usr_id xsi:type=\"xsd:int\">\\d*</usr_id>", "\\D*", new TwoObjects("sid", sessionId)));
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
		for (val x : objects.getObjects()) {
			if (x.getTitle().contains(ID_PREFIX)) {
				Long id = Long.parseLong(x.getTitle().substring(ID_PREFIX.length()));
				if (id != null) {
					result.add(id);
				}
			}
		}

		return result;
	}

	//	public List<Long> getCourseIds(long userId){
	//		return parseLongs(sendSOAPMessageListGetValueByPattern("getUserRoles", "Title&gt;il_crs_member_\\d*&lt;/Title", "\\D*", new TwoObjects("sid", sessionId), new TwoObjects("user_id", userId)));
	//	}

	//	private List<Long> parseLongs(final java.util.List<java.lang.String> list) {
	//		val result = new LinkedList<Long>();
	//		for(val s : list){
	//			result.add(Long.parseLong(s));
	//		}
	//		return result;
	//	}

	//	public String sendSOAPMessageGetValueByPattern(String soapMethodName, String patternRegex, String replacer, TwoObjects<String, Object>... mapNameToValue) {
	//		return sendSOAPMessageListGetValueByPattern(soapMethodName, patternRegex, replacer, mapNameToValue).get(0);
	//	}

	//	public List<String> sendSOAPMessageListGetValueByPattern(String soapMethodName, String patternRegex, String replacer, TwoObjects<String, Object>... mapNameToValue) {
	//		String s = sendSOAPMessage(soapMethodName, mapNameToValue);
	//
	//		val result = new LinkedList<String>();
	//
	//		Pattern p = Pattern.compile(patternRegex);
	//		Matcher m = p.matcher(s);
	//
	//		while(m.find()){
	//			result.add(m.group(0).replaceAll(replacer, ""));
	//		}
	//
	//		return result;
	//	}

	//	public InputStream sendSOAPMessageGetStreamBetween(String beginString, String endString, String soapMethodName, TwoObjects<?, ?>... mapNameToValue) {
	//		try{
	//			InputStream in = sendSoapRequestGetInputStream(soapMethodName, mapNameToValue);
	//
	//			StringBuffer s = new StringBuffer();
	//			int line;
	//			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
	//			while((line = reader.read()) != -1){
	//				s.append((char) line);
	//				if(s.)
	//			}
	//
	//			return s.toString();
	//		}catch(Exception e){
	//			throw new IliasException(e);
	//		}
	//	}
	//	public String sendSOAPMessage(String soapMethodName, TwoObjects<?, ?>... mapNameToValue) {
	//		try{
	//			InputStream in = sendSoapRequestGetInputStream(soapMethodName, mapNameToValue);
	//
	//			StringBuffer s = new StringBuffer();
	//			String line;
	//			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
	//			while((line = reader.readLine()) != null){
	//				s.append(line);
	//			}
	//
	//			return s.toString();
	//		}catch(Exception e){
	//			throw new IliasException(e);
	//		}
	//	}
	private String sendSoapRequest(String soapMethodName, TwoObjectsX<?, ?>... mapNameToValue) {
		//		log.debug("Received Payload Content: " + s);
		return sendSoapRequestGetSoapBody(soapMethodName, mapNameToValue).getText();
	}

	private SOAPResult sendSoapRequestGetSoapBody(String soapMethodName, TwoObjectsX<?, ?>... mapNameToValue) {
		SoapObject soapObject = new SoapObject("http://schemas.xmlsoap.org/soap/envelope/", soapMethodName);
		//		soapObject.
		//		soapObject.addProperty(documentIdsPropertyInfo);
		//		soapObject.addProperty("pluginType", "another string");
		//		soapObject.addProperty("xmlConfiguration", "next string");

		for (val nameToValue : mapNameToValue) {
			PropertyInfo propertyInfo = new PropertyInfo();
			propertyInfo.setName(String.valueOf(nameToValue.getObjectA()));
			propertyInfo.setValue(String.valueOf(nameToValue.getObjectB()));
			propertyInfo.setType(nameToValue.getObjectB().getClass());
			soapObject.addProperty(propertyInfo);
		}

		SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		//		soapEnvelope.dotNet = true;
		soapEnvelope.setOutputSoapObject(soapObject);

		//         soapEnvelope.addMapping(NAMESPACE, "documentIds", new DocumentIDs().getClass());

		SOAPResult result = new SOAPResult();
		result.setFaultCode(false);

		HttpTransportSE http = new HttpTransportSE(soapServerURL);
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

//		SOAPResult sendSoapRequestGetSoapBody_Old = sendSoapRequestGetSoapBody_Old(soapMethodName, mapNameToValue);
//		System.out.println(sendSoapRequestGetSoapBody_Old);
//		return sendSoapRequestGetSoapBody_Old;


	}

//	private SOAPResult sendSoapRequestGetSoapBody_Old(String soapMethodName, TwoObjectsX<?, ?>... mapNameToValue) {
//		try {
//
//			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
//			SOAPConnection soapConnection = soapConnectionFactory.createConnection();
//
//			// Send SOAP Message to SOAP Server
//			String url = soapServerURL;
//
//			MessageFactory messageFactory = MessageFactory.newInstance();
//			final SOAPMessage soapMessage = messageFactory.createMessage();
//			SOAPPart soapPart = soapMessage.getSOAPPart();
//			soapPart.setStrictErrorChecking(false);
//
//			//			String serverURI = "http://www.ilias.fh-dortmund.de/";
//
//			// SOAP Envelope
//			SOAPEnvelope envelope = soapPart.getEnvelope();
//			envelope.addNamespaceDeclaration("xmlns", "http://schemas.xmlsoap.org/soap/envelope/");
//
//			/*
//		    Constructed SOAP Request Message:
//		    <SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/" xmlns:example="http://ws.cdyne.com/">
//		        <SOAP-ENV:Header/>
//		        <SOAP-ENV:Body>
//		            <example:VerifyEmail>
//		                <example:email>mutantninja@gmail.com</example:email>
//		                <example:LicenseKey>123</example:LicenseKey>
//		            </example:VerifyEmail>
//		        </SOAP-ENV:Body>
//		    </SOAP-ENV:Envelope>
//			 */
//
//			// SOAP Body
//			SOAPBody soapBody = envelope.getBody();
//			SOAPElement soapBodyElem;
//			soapBodyElem = soapBody.addChildElement(soapMethodName);
//
//			//		soapBody.addNamespaceDeclaration("xmlns", "urn:ilUserAdministration");
//
//			for(val element : mapNameToValue){
//				SOAPElement soapBodyElem1 = soapBodyElem.addChildElement(String.valueOf(element.getObjectA()));
//				soapBodyElem1.addTextNode(String.valueOf(element.getObjectB()));
//
//			}
//
//			//					SOAPElement soapBodyElem1 = soapBodyElem.addChildElement("client");
//			//					soapBodyElem1.addTextNode("ilias-fhdo");
//			//			
//			//					soapBodyElem1 = soapBodyElem.addChildElement("username");
//			//					soapBodyElem1.addTextNode("kekru001");
//			//					soapBodyElem1 = soapBodyElem.addChildElement("password");
//			//					soapBodyElem1.addTextNode("");
//
//
//
//
//			MimeHeaders headers = soapMessage.getMimeHeaders(); 
//			headers.addHeader("SOAPAction", soapServerURL+"/"+soapMethodName);
//			//			headers.addHeader("SOAPAction", serverURI  + soapMethodName);
//
//			soapMessage.saveChanges();
//
//
//			//Gesendete Nachricht anzeigen
//			//			try {
//			//				Class<?> clazz = soapMessage.getClass();
//			//				Field f = null;
//			//				while(clazz != null){
//			//					try{
//			//						f = clazz.getDeclaredField("messageBytes");
//			//						break;
//			//					} catch (NoSuchFieldException e1) {
//			//
//			//					}
//			//
//			//					clazz = clazz.getSuperclass();
//			//				}
//			//				//			com.sun.xml.internal.messaging.saaj.soap.MessageImpl;
//			//				//						com.sun.xml.internal.messaging.saaj.soap.ver1_1.Message1_1Impl;
//			//
//			//				//			Field f = com.sun.xml.internal.messaging.saaj.soap.MessageImpl.class.getDeclaredField("messageBytes");
//			//				f.setAccessible(true);
//			//				//				log.debug("Sending Soap Message: " + new String((byte[]) f.get(soapMessage)));
//			//			} catch (Exception e) {
//			//
//			//			}
//
//			final SOAPMessage soapResponse = soapConnection.call(soapMessage, url);
//			//		soapResponse.getContentDescription();
//			//			soapConnection.close();
//
//			soapBody = soapResponse.getSOAPBody();
//
//			soapConnection.close();
//
//			SOAPResult sr = new SOAPResult();
//			Node firstChild = soapBody.getFirstChild();
//			sr.setText(firstChild.getFirstChild().getTextContent());
//			sr.setError(firstChild.getChildNodes().getLength() >= 2 ? firstChild.getChildNodes().item(1).getTextContent() : null);
//			sr.setFaultCode(firstChild.getNodeName().contains("faultcode"));
//
//			return sr;
//
//		} catch (SOAPException e2) {
//			throw new IliasException(e2);
//		}
//
//	}

}
