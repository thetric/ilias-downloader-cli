package de.adesso.azubis.iliasdownloader.v3.sync;

import de.adesso.iliasdownloader2.service.ILIASSoapService;
import de.adesso.iliasdownloader3.service.model.LoginCredentials;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import lombok.AllArgsConstructor;
import lombok.NonNull;

/**
 * Service für den Ilias Login.
 *
 * @author Dominik Broj
 * @since 01.02.2016
 */
@AllArgsConstructor
public final class IliasLoginService extends Service<Void> {
    @NonNull
    private final ILIASSoapService iliasSoapService;
    @NonNull
    private final LoginCredentials loginCredentials;

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                iliasSoapService.login(loginCredentials);
                return null;
            }
        };
    }
}
