package com.github.thetric.iliasdownloader.cli

import com.github.thetric.iliasdownloader.connector.api.IliasService

/**
 * Creates a [IliasService] either from previous settings or shows a setup
 * wizard.
 */
internal interface LoginService {
    /**
     * Creates a new connected [IliasService].
     * The settings for the connection are applied either from previous settings
     * (if available) or from a setup dialog.
     * In the latter case the preferences are saved.
     *
     * @return connected [IliasService]
     */
    fun connect(): IliasService
}
