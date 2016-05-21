package de.adesso.azubis.iliasdownloader.v3.ui.intro;

import de.adesso.azubis.iliasdownloader.v3.ui.util.FxmlLoaderHelper;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import lombok.extern.log4j.Log4j2;
import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.WizardPane;

import java.io.IOException;

/**
 * Created by Dominik Broj on 30.01.2016.
 *
 * @author Dominik Broj
 * @since 30.01.2016
 */
@Log4j2
final class WelcomeWizardPane extends WizardPane {
    private static final String FXML_FILE = "/fxml/messagePane.fxml";

    @FXML
    private Label message;

    public WelcomeWizardPane() throws IOException {
        Parent content = FxmlLoaderHelper.load(this, FXML_FILE);
        setContent(content);
        setHeaderText("Willkommen beim ILIAS Downloader 3!");

        message.setText("Willkommen beim ILIAS Downloader 3.0.1-alpha!\n" +
                "Mit dem folgenden Wizard werden die grundlegenden Einstellungen festgelegt. Diese können später über das Menü bearbeitet werden.");
    }

    @Override
    public void onExitingPage(Wizard wizard) {
    }

    @Override
    public void onEnteringPage(Wizard wizard) {
        wizard.setTitle("Ilias Downloader 3 - Willkommen");
        wizard.setInvalid(false);
    }
}
