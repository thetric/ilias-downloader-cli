package com.github.thetric.iliasdownloader.cli

import com.github.thetric.iliasdownloader.service.IliasService

/**
 * Creates a {@link IliasService} either from previous settings or shows a setup wizard.
 */
interface LoginService {
    /**
     * Creates a new connected {@link IliasService}.
     * The settings for the connection are applied either from previous settings (if available) or from a setup dialog.
     * In the latter case the preferences are saved.
     * @return connected {@link IliasService}
     */
    IliasService connect()
}
