package com.github.thetric.iliasdownloader.connector.api

/**
 * Encapsulates the creation of [IliasService]s. All implementations are encouraged to implement this to simplify the
 * instantiation.
 */
interface IliasServiceProvider {
    /**
     * Creates a new [IliasService].
     *
     * @return a new [IliasService]
     */
    fun newInstance(): IliasService
}
