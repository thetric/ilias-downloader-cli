package com.github.thetric.iliasdownloader.ui.jfx.ui.intro.setup

import com.github.thetric.iliasdownloader.service.IliasService
import groovy.transform.Canonical
/**
 * @author broj
 */
@Canonical
class ConnectionSetupData {
    IliasService iliasService
    String loginUrl
}
