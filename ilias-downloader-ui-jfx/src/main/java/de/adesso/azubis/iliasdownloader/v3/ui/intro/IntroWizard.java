package de.adesso.azubis.iliasdownloader.v3.ui.intro;

import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.WizardPane;

import java.io.IOException;

/**
 * Created by Dominik Broj on 30.01.2016.
 *
 * @author Dominik Broj
 * @since 30.01.2016
 */
public final class IntroWizard extends Wizard {
    public IntroWizard() throws IOException {
        final WelcomeWizardPane page0 = new WelcomeWizardPane();
        WizardPane page1 = new LicenseWizardPane();
        final ServerSettingsWizardPane page2 = new ServerSettingsWizardPane();
        final FinishWizardPane page3 = new FinishWizardPane();
        setFlow(new Wizard.LinearFlow(page0, page1, page2, page3));
    }
}
