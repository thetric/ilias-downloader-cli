package com.github.thetric.iliasdownloader.ui.jfx.sync

import lombok.Value
/**
 * Created by Dominik Broj on 02.02.2016.
 *
 * @author Dominik Broj
 * @since 02.02.2016
 */
@Value
final class SyncSettings {
    private final String baseDir;
    private final boolean syncOnly;
    private final Set<Long> ignoredFileIds;
}
