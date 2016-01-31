package de.adesso.azubis.iliasdownloader.v3.ui.intro;

import de.adesso.azubis.iliasdownloader.v3.ui.util.FxmlLoaderHelper;
import javafx.fxml.FXML;
import javafx.scene.Parent;
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
final class WelcomeWizardPane extends WizardPane {
	private static final String FXML_FILE = "/fxml/welcome.fxml";
	private static final String WELCOME_HTML= "/html/welcome.html";

	@FXML
	private WebView webView;

	public WelcomeWizardPane() throws IOException {
		Parent content = FxmlLoaderHelper.load(this, FXML_FILE);
		setContent(content);
		setHeaderText("Willkommen beim ILIAS Downloader 3!");

		final String licenseUrl = WelcomeWizardPane.class.getResource(WELCOME_HTML).toExternalForm();
		final WebEngine engine = webView.getEngine();
		engine.setUserStyleSheetLocation(getClass().getResource("/html/bootstrap-3.3.6.min.css").toString());
		engine.load(licenseUrl);
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
