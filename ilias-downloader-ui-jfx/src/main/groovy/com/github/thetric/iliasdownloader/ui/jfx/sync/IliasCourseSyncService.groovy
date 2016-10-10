package com.github.thetric.iliasdownloader.ui.jfx.sync

import com.github.thetric.iliasdownloader.service.IliasService
import javafx.concurrent.Service
import javafx.concurrent.Task
import lombok.AllArgsConstructor
import lombok.NonNull

/**
 * Created by Dominik Broj on 02.02.2016.
 *
 * @author Dominik Broj
 * @since 02.02.2016
 */
@AllArgsConstructor
final class IliasCourseSyncService extends Service<Void> {
    @NonNull
    private final IliasService soapService;

    @NonNull
    private final SyncSettings syncSettings;

    @Override
    protected Task<Void> createTask() {
        return null;
    }
}
