package de.adesso.iliasdownloader3.service;

import de.adesso.iliasdownloader3.service.exception.IliasException;
import de.adesso.iliasdownloader3.service.exception.IliasHttpsException;
import lombok.experimental.UtilityClass;
import lombok.val;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Deprecated
@UtilityClass
public class IliasUtil {
    private static final String LOGIN_PHP = "login.php";
    private static final String WEBSERVICE_SOAP_SERVER_PHP = "webservice/soap/server.php";
    private static final String WEBDAV_PHP = "webdav.php";

    public static String findWebdavByWebservice(String url) {
        if (url.contains(WEBDAV_PHP)) {
            return url;
        }

        return getIliasInstallationURL(url) + "/" + WEBDAV_PHP;
    }

    public static String getIliasInstallationURL(String webserviceURL) {
        return webserviceURL.substring(0, webserviceURL.lastIndexOf("/" + WEBSERVICE_SOAP_SERVER_PHP));
    }

    public static String findSOAPWebserviceByLoginPage(String loginURL) {

        if (loginURL.endsWith(WEBSERVICE_SOAP_SERVER_PHP)) {
            return loginURL;
        }

        return replacePath(loginURL, LOGIN_PHP, WEBSERVICE_SOAP_SERVER_PHP);
    }

    public static String findLoginPageBySOAPWebservice(String soapServerURL) {

        if (soapServerURL.contains(LOGIN_PHP)) {
            return soapServerURL;
        }

        return replacePath(soapServerURL, WEBSERVICE_SOAP_SERVER_PHP, LOGIN_PHP);
    }

    private static String replacePath(String soapServerURL, String currentPostfix, String newPostfix) {
        int index = soapServerURL.lastIndexOf(currentPostfix);

        if (index == -1) {
            throw new IliasException("The URL to the webservice needs to contain '" + currentPostfix + "' but it was " + soapServerURL);
        }

        soapServerURL = soapServerURL.substring(0, index);
        if (!soapServerURL.endsWith("/")) {
            soapServerURL += "/";
        }

        return soapServerURL + newPostfix;
    }

    public static String findClientByLoginPageOrWebserviceURL(String loginURL) {
        loginURL = findLoginPageBySOAPWebservice(loginURL);

        val prefix = "ilClientId=";
        val postfix = ";";
        Pattern p = Pattern.compile(prefix + "[^" + postfix + "]*" + postfix);
        try {
            URLConnection connection = new URL(loginURL).openConnection();
            List<String> cookies = connection.getHeaderFields().get("Set-Cookie");

            if (cookies == null) {
                InputStream in = null;
                try {
                    in = connection.getInputStream();
                } catch (SSLException e) {
                    throwDefaultHTTPSException(e);
                }
                try {
                    in.close();
                } catch (Exception e) {

                }
            }

            for (String s : cookies) {
                Matcher m = p.matcher(s);

                if (m.find()) {
                    s = m.group(0);
                    return s.substring(0, s.lastIndexOf(postfix)).substring(prefix.length());
                }

            }
        } catch (NullPointerException | IOException e) {
            throw new IliasException(e);
        }

        throw new IliasException("Client Id not found");
    }

    public static void throwDefaultHTTPSException(Throwable cause) {
        throw new IliasHttpsException("Could not generate SSL Session, try using Java 1.8 or higher, you use Java " + System.getProperty("java.version"), cause);
    }
}
