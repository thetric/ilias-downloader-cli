package com.github.thetric.iliasdownloader.ui.jfx.ui.intro.setup

import com.github.thetric.iliasdownloader.service.IliasService
import com.github.thetric.iliasdownloader.service.webparser.WebParserIliasServiceProvider
import com.github.thetric.iliasdownloader.ui.jfx.ui.util.DialogHelper
import javafx.scene.control.TextInputDialog
import lombok.NonNull
import lombok.extern.log4j.Log4j2
/**
 * @author broj
 * @since 25.09.2016
 */
@Log4j2
final class WebIliasSetupController {

    Optional<IliasService> getIliasService(@NonNull String loginPage) {
        if (loginPage.isEmpty()) {
            Optional<String> loginOptional = getLoginPage();
            if (loginOptional.isPresent()) {
                loginPage = loginOptional.get();
            } else {
                // user has canceled
                return Optional.empty();
            }
        }

        try {
            WebParserIliasServiceProvider serviceProvider = new WebParserIliasServiceProvider(loginPage);
            return Optional.of(serviceProvider.newInstance());
        } catch (Exception e) {
            log.error("Konnte den Ilias Service Provider nicht erstellen", e);
            DialogHelper.showExceptionDialog("Fehler beim Erstellen des Ilias Service Providers", e);
        }
        return getIliasService("");
    }

    private static Optional<String> getLoginPage() {
        TextInputDialog inputDialog = new TextInputDialog();
        inputDialog.setHeaderText("Ilias Login Seite eingeben");
        inputDialog.setTitle("Ilias Downloader 3 - Einrichtung");
        return inputDialog.showAndWait();
    }

}
