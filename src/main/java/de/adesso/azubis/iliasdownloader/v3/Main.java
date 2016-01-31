package de.adesso.azubis.iliasdownloader.v3;

import de.adesso.azubis.iliasdownloader.v3.ui.intro.IntroWizard;
import javafx.application.Application;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;

/**
 * Startklasse der Anwendung.
 *
 * @author Dominik Broj
 * @since 30.01.2016
 */
@Log4j2
public final class Main extends Application {
	public static void main(String[] args) {
		Main.launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws IOException {
		log.info("Creating intro wizard...");
		new IntroWizard();
	}
}
