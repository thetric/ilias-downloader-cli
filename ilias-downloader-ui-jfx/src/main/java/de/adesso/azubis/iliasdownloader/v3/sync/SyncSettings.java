package de.adesso.azubis.iliasdownloader.v3.sync;

import lombok.Value;

import java.util.Set;

/**
 * Created by Dominik Broj on 02.02.2016.
 *
 * @author Dominik Broj
 * @since 02.02.2016
 */
@Value
public final class SyncSettings {
    private final String baseDir;
    private final boolean syncOnly;
    private final Set<Long> ignoredFileIds;
}
