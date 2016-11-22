package com.github.thetric.iliasdownloader.ui.jfx.sync

import com.github.thetric.iliasdownloader.service.IliasService
import groovy.transform.TupleConstructor
import javafx.concurrent.Service
import javafx.concurrent.Task

/**
 * Created by Dominik Broj on 02.02.2016.
 *
 * @author Dominik Broj
 * @since 02.02.2016
 */
@TupleConstructor
final class IliasCourseSyncService extends Service<Void> {
    private final IliasService soapService
    private final SyncSettings syncSettings

    @Override
    protected Task<Void> createTask() {
        return null;
    }
}
