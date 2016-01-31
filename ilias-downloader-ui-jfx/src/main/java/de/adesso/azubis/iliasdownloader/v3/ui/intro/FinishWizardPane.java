package de.adesso.azubis.iliasdownloader.v3.ui.intro;

import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.WizardPane;

/**
 * Created by Dominik Broj on 31.01.2016.
 *
 * @author Dominik Broj
 * @since 31.01.2016
 */
final class FinishWizardPane extends WizardPane {
	@Override
	public void onEnteringPage(Wizard wizard) {
		wizard.setTitle("Ilias Downloader 3 - Einrichtung abgeschlossen");
	}
}
