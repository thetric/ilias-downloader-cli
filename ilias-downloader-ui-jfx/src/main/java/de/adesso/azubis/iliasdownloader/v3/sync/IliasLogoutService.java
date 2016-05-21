package de.adesso.azubis.iliasdownloader.v3.sync;

import de.adesso.iliasdownloader2.service.ILIASSoapService;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import lombok.AllArgsConstructor;
import lombok.NonNull;

/**
 * Service für den Ilias Logout.
 *
 * @author Dominik Broj
 * @since 01.02.2016
 */
@AllArgsConstructor
public final class IliasLogoutService extends Service<Void> {
    @NonNull
    private final ILIASSoapService iliasSoapService;

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                iliasSoapService.logout();
                return null;
            }
        };
    }
}
