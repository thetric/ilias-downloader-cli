package com.github.thetric.iliasdownloader.ui.jfx.sync

import com.github.thetric.iliasdownloader.service.IliasService
import groovy.transform.TupleConstructor
import javafx.concurrent.Service
import javafx.concurrent.Task

/**
 * Service für den Ilias Logout.
 *
 * @author Dominik Broj
 * @since 01.02.2016
 */
@TupleConstructor
final class IliasLogoutService extends Service<Void> {
    private final IliasService iliasSoapService

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                iliasSoapService.logout()
                return null
            }
        }
    }
}
