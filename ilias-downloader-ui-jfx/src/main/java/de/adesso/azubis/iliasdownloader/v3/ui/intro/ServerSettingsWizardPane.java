package de.adesso.azubis.iliasdownloader.v3.ui.intro;

import de.adesso.azubis.iliasdownloader.v3.ui.util.FxmlLoaderHelper;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.WizardPane;

import java.io.IOException;

/**
 * Created by Dominik Broj on 30.01.2016.
 *
 * @author Dominik Broj
 * @since 30.01.2016
 */
final class ServerSettingsWizardPane extends WizardPane {
	private static final String FXML_FILE = "/fxml/serverSettings.fxml";

	@FXML
	private TextField iliasUrlField, clientIdField;
	@FXML
	private ComboBox<?> loginMethodsBox;
	@FXML
	private ComboBox<?> downloadMethodsBox;
	@FXML
	private Button getClientIdButton;

	public ServerSettingsWizardPane() throws IOException {
		Parent content = FxmlLoaderHelper.load(this, FXML_FILE);
		setContent(content);
		setHeaderText("Verbindungseinstellungen");
	}

	@Override
	public void onEnteringPage(Wizard wizard) {
		wizard.setTitle("Ilias Downloader 3 - ILIAS Servereinstellungen");
//		wizard.setInvalid(true);
	}

	@Override
	public void onExitingPage(Wizard wizard) {
		wizard.setInvalid(false);
		wizard.invalidProperty().unbind();
	}
}
