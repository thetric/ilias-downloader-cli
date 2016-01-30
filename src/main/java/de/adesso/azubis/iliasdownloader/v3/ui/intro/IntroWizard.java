package de.adesso.azubis.iliasdownloader.v3.ui.intro;

import javafx.scene.control.ButtonType;
import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.WizardPane;

import java.io.IOException;

/**
 * Created by Dominik Broj on 30.01.2016.
 *
 * @author Dominik Broj
 * @since 30.01.2016
 */
public final class IntroWizard {

	public IntroWizard() throws IOException {
		// Create pages. Here for simplicity we just create and instance of WizardPane.
		WizardPane page1 = new LicenseWizardPane();
		WizardPane page2 = new WizardPane();
		WizardPane page3 = new WizardPane();

		// create wizard
		Wizard wizard = new Wizard();

		// create and assign the flow
		wizard.setFlow(new Wizard.LinearFlow(page1, page2, page3));

		// show wizard and wait for response
		wizard.showAndWait().ifPresent(result -> {
			if (result == ButtonType.FINISH) {
				System.out.println("Wizard finished, settings: " + wizard.getSettings());
			}
		});
	}
}
