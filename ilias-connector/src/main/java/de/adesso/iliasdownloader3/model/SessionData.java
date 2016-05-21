package de.adesso.iliasdownloader3.model;

import lombok.Value;

/**
 * Created by Dominik Broj on 02.02.2016.
 *
 * @author Dominik Broj
 * @since 02.02.2016
 */
@Value
final class SessionData {
	private final String sessionId;
	private final long userId;
}
