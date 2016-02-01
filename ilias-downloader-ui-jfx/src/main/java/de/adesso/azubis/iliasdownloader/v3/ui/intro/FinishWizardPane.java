package de.adesso.azubis.iliasdownloader.v3.ui.intro;

import de.adesso.azubis.iliasdownloader.v3.ui.util.FxmlLoaderHelper;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.WizardPane;

import java.io.IOException;

/**
 * Created by Dominik Broj on 31.01.2016.
 *
 * @author Dominik Broj
 * @since 31.01.2016
 */
final class FinishWizardPane extends WizardPane {
	private static final String FXML_FILE = "/fxml/messagePane.fxml";

	@FXML
	private Label message;

	public FinishWizardPane() throws IOException {
		Parent content = FxmlLoaderHelper.load(this, FXML_FILE);
		setContent(content);
		setHeaderText("Die Einrichtigung ist abgeschlossen");

		message.setText("Die Einrichtung ist soweit abgeschlossen!\n" +
				"Die Einstellungen können später auch noch im Programm geändert werden.");
	}

	@Override
	public void onEnteringPage(Wizard wizard) {
		wizard.setTitle("Ilias Downloader 3 - Einrichtung abgeschlossen");
	}
}
