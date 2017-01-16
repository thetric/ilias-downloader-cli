package com.github.thetric.iliasdownloader.ui.jfx.sync

import groovy.transform.Immutable

/**
 * Created by Dominik Broj on 02.02.2016.
 *
 * @author Dominik Broj
 * @since 02.02.2016
 */
@Immutable
class SyncSettings {
    String baseDir
    boolean syncOnly
    Set<Long> ignoredFileIds
}
