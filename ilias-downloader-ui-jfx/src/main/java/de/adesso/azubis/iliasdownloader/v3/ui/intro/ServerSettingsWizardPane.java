package de.adesso.azubis.iliasdownloader.v3.ui.intro;

import de.adesso.azubis.iliasdownloader.v3.ui.util.FxmlLoaderHelper;
import de.adesso.iliasdownloader2.service.IliasUtil;
import de.adesso.iliasdownloader3.exception.IliasException;
import de.adesso.iliasdownloader3.model.DownloadMethod;
import de.adesso.iliasdownloader3.model.LoginType;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
final class ServerSettingsWizardPane extends WizardPane {
	private static final String FXML_FILE = "/fxml/serverSettings.fxml";

	@FXML
	private TextField iliasUrlField, clientIdField;
	@FXML
	private ComboBox<LoginType> loginMethodsBox;
	@FXML
	private ComboBox<DownloadMethod> downloadMethodsBox;
	@FXML
	private Button getClientIdButton;

	@FXML
	private Label iliasServerUrlValidationLabel;

	public ServerSettingsWizardPane() throws IOException {
		Parent content = FxmlLoaderHelper.load(this, FXML_FILE);
		setContent(content);
		setHeaderText("Verbindungseinstellungen");

		loginMethodsBox.getItems().setAll(LoginType.values());
		loginMethodsBox.getSelectionModel().select(LoginType.LDAP);
		downloadMethodsBox.getItems().setAll(DownloadMethod.values());
		downloadMethodsBox.getSelectionModel().select(DownloadMethod.WEBSERVICE);
	}

	private void checkIliasServerUrl(Wizard wizard, String url) {
		boolean error = false;
		try {
			final String clientId = IliasUtil.findClientByLoginPageOrWebserviceURL(url);
			clientIdField.setText(clientId);
			iliasServerUrlValidationLabel.setText("Die Ilias Loginseite ist gültig");
		} catch (IliasException iliasEx) {
			log.error("Ungültige Ilias Url", iliasEx.getMessage());
			error = true;
			iliasServerUrlValidationLabel.setText("Trage bitte eine gültige URL für die Ilias Loginseite ein und drücke Enter");
		}
		wizard.setInvalid(error);
	}

	@Override
	public void onEnteringPage(Wizard wizard) {
		wizard.setTitle("Ilias Downloader 3 - ILIAS Servereinstellungen");

		checkIliasServerUrl(wizard, iliasUrlField.getText());
		iliasUrlField.requestFocus();

		final EventHandler<ActionEvent> checkIliasUrlHandler = e -> checkIliasServerUrl(wizard, iliasUrlField.getText());
		iliasUrlField.setOnAction(checkIliasUrlHandler);
		getClientIdButton.setOnAction(checkIliasUrlHandler);
	}

	@Override
	public void onExitingPage(Wizard wizard) {
		wizard.setInvalid(false);
		wizard.invalidProperty().unbind();
	}
}
