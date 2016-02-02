package de.adesso.azubis.iliasdownloader.v3.sync;

import de.adesso.iliasdownloader2.service.ILIASSoapService;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import lombok.AllArgsConstructor;
import lombok.NonNull;

/**
 * Created by Dominik Broj on 02.02.2016.
 *
 * @author Dominik Broj
 * @since 02.02.2016
 */
@AllArgsConstructor
public final class IliasCourseSyncService extends Service<Void> {
	@NonNull
	private final ILIASSoapService soapService;

	@NonNull
	private final SyncSettings syncSettings;

	@Override
	protected Task<Void> createTask() {
		return null;
	}
}
