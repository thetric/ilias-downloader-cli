package com.github.thetric.iliasdownloader.ui.jfx.sync

import com.github.thetric.iliasdownloader.service.IliasService
import com.github.thetric.iliasdownloader.service.model.LoginCredentials
import groovy.transform.TupleConstructor
import javafx.concurrent.Service
import javafx.concurrent.Task

/**
 * Service für den Ilias Login.
 *
 * @author Dominik Broj
 * @since 01.02.2016
 */
@TupleConstructor
final class IliasLoginService extends Service<Void> {
    private final IliasService iliasSoapService
    private final LoginCredentials loginCredentials

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
