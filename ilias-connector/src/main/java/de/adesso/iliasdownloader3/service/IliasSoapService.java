package de.adesso.iliasdownloader3.service;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Created by Dominik Broj on 02.02.2016.
 *
 * @author Dominik Broj
 * @since 02.02.2016
 */
public interface IliasSoapService {
	void login(LoginData loginData) throws IOException, XmlPullParserException;
}
