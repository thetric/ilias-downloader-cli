package com.github.thetric.iliasdownloader.ui.jfx.ui.intro.setup

import com.github.thetric.iliasdownloader.service.IliasService
import com.github.thetric.iliasdownloader.service.webparser.WebParserIliasServiceProvider
import com.github.thetric.iliasdownloader.ui.jfx.ui.util.DialogHelper
import groovy.transform.CompileStatic
import groovy.util.logging.Log4j2
import javafx.scene.control.TextInputDialog

/**
 * @author broj
 * @since 25.09.2016
 */
@CompileStatic
@Log4j2
final class WebIliasSetupController {

    Optional<IliasService> getIliasService(String loginPage) {
        if (loginPage == null || loginPage.empty) {
            Optional<String> loginOptional = getLoginPage()
            if (loginOptional.present) {
                loginPage = loginOptional.get()
            } else {
                // user has canceled
                return Optional.empty()
            }
        }

        try {
            WebParserIliasServiceProvider serviceProvider = new WebParserIliasServiceProvider(loginPage)
            return Optional.of(serviceProvider.newInstance())
        } catch (Exception e) {
            log.error('Konnte den Ilias Service Provider nicht erstellen', e)
            DialogHelper.showExceptionDialog('Fehler beim Erstellen des Ilias Service Providers', e)
        }
        return getIliasService('')
    }

    private static Optional<String> getLoginPage() {
        def inputDialog = new TextInputDialog()
        inputDialog.headerText = 'Ilias Login Seite eingeben'
        inputDialog.title = 'Ilias Downloader 3 - Einrichtung'
        return inputDialog.showAndWait()
    }
}
