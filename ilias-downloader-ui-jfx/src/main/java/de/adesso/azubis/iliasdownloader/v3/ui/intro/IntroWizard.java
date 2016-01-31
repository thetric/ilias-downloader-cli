package de.adesso.azubis.iliasdownloader.v3.ui.intro;

import javafx.scene.control.ButtonType;
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
public final class IntroWizard {
	public IntroWizard() throws IOException {
		final WelcomeWizardPane page0 = new WelcomeWizardPane();
		WizardPane page1 = new LicenseWizardPane();
		final ServerSettingsWizardPane page2 = new ServerSettingsWizardPane();
		final FinishWizardPane page3 = new FinishWizardPane();

		Wizard wizard = new Wizard();
		wizard.setFlow(new Wizard.LinearFlow(page0, page1, page2, page3));
		wizard.showAndWait().ifPresent(result -> {
			if (result == ButtonType.FINISH) {
				System.out.println("Wizard finished, settings: " + wizard.getSettings());
			}
		});
	}
}
