package de.adesso.azubis.iliasdownloader.v3.ui.intro;

import de.adesso.azubis.iliasdownloader.v3.ui.util.FxmlLoaderHelper;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
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
final class LicenseWizardPane extends WizardPane {
	private static final String FXML_FILE = "/fxml/disclaimer.fxml";

	private static final String LICENSE_HTML = "/html/license.html";

	@FXML
	private WebView licenseWebview;
	@FXML
	private CheckBox licenseAgreementCheckbox;

	LicenseWizardPane() throws IOException {
		Parent content = FxmlLoaderHelper.load(this, FXML_FILE);
		setContent(content);
		setHeaderText("Nutzungsbedingungen");

		licenseAgreementCheckbox.setText("Ich akzeptiere die hier aufgeführten Bedingungen");

		final String licenseUrl = LicenseWizardPane.class.getResource(LICENSE_HTML).toExternalForm();
		final WebEngine engine = licenseWebview.getEngine();
		engine.setUserStyleSheetLocation(getClass().getResource("/html/bootstrap-3.3.6.min.css").toString());
		engine.load(licenseUrl);
	}

	@Override
	public void onEnteringPage(Wizard wizard) {
		wizard.setTitle("Ilias Downloader 3 - Lizenz");
		wizard.setInvalid(!licenseAgreementCheckbox.isSelected());
		licenseAgreementCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> {
			wizard.setInvalid(!newValue);
		});
	}

	@Override
	public void onExitingPage(Wizard wizard) {
		wizard.setInvalid(false);
	}
}
