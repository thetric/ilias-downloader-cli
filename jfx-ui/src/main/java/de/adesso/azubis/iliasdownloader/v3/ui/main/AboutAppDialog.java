package de.adesso.azubis.iliasdownloader.v3.ui.main;

import de.adesso.azubis.iliasdownloader.v3.ui.util.FxmlLoaderHelper;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.io.IOException;

import static javafx.scene.control.ButtonType.CLOSE;

/**
 * Created by Dominik Broj on 05.02.2016.
 *
 * @author Dominik Broj
 * @since 05.02.2016
 */
final class AboutAppDialog extends Dialog<ButtonType> {
    @FXML
    private WebView aboutAppWebView;

    public AboutAppDialog() throws IOException {
        setTitle("Über diese Anwendung");
        setHeaderText("Über Ilias Downloader 3");
        final DialogPane dialogPane = getDialogPane();
        dialogPane.getButtonTypes().setAll(CLOSE);

        final Parent rootPane = FxmlLoaderHelper.load(this, "/fxml/aboutApp.fxml");

        final String aboutApp = AboutAppDialog.class.getResource("/html/aboutApp.html").toExternalForm();
        final WebEngine engine = aboutAppWebView.getEngine();
        engine.setUserStyleSheetLocation(getClass().getResource("/html/bootstrap-3.3.6.min.css").toString());
        engine.load(aboutApp);

        dialogPane.setContent(aboutAppWebView);

    }
}
