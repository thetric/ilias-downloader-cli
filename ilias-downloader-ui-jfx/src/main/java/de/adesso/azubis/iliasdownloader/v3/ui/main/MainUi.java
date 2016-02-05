package de.adesso.azubis.iliasdownloader.v3.ui.main;

import de.adesso.azubis.iliasdownloader.v3.ui.util.FxmlLoaderHelper;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.controlsfx.control.ToggleSwitch;

import java.io.IOException;

import static de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon.*;

/**
 * Created by Dominik Broj on 05.02.2016.
 *
 * @author Dominik Broj
 * @since 05.02.2016
 */
public final class MainUi extends Application {
	private final Stage stage;
	private final BorderPane rootPane;

	@FXML
	private ToolBar toolBar;

	@FXML
	private Button syncButton;
	@FXML
	private ToggleSwitch downloadToggleSwitch;

	@FXML
	private MenuButton settingsMenuButton;
	@FXML
	private MenuItem connectSettingsItem, downloadSettingsItem, helpItem;
	@FXML
	private MenuItem aboutAppItem, feedbackItem, goToWebsiteItem, goToAndroidAppItem;
	@FXML
	private TreeTableColumn<?, ?> syncTreeTableView;

	public MainUi() throws IOException {
		rootPane = (BorderPane) FxmlLoaderHelper.load(this, "/fxml/mainWindow.fxml");

		initGraphics();
		setEventHandlers();

		final Scene scene = new Scene(rootPane);
		stage = new Stage();
		stage.getIcons().setAll(new Image("/icons/app-icon-64x64.png"),
				new Image("/icons/app-icon-32x32.png"), new Image("/icons/app-icon-16x16.png"));
		stage.setScene(scene);
		stage.setTitle("Ilias Downloader 3");
		stage.show();
	}

	private void setEventHandlers() {
		aboutAppItem.setOnAction(e -> {
			try {
				new AboutAppDialog().show();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
	}

	public static void main(String[] args) {
		launch(MainUi.class);
	}

	private static void setIcon(Labeled labeled, MaterialDesignIcon mdIcon) {
		final MaterialDesignIconView icon = new MaterialDesignIconView(mdIcon);
		labeled.setGraphic(icon);
	}

	private static void setIcon(MenuItem menuItem, MaterialDesignIcon mdIcon) {
		final MaterialDesignIconView icon = new MaterialDesignIconView(mdIcon);
		menuItem.setGraphic(icon);
	}

	private void initGraphics() {
		setIcon(settingsMenuButton, SETTINGS);
		setIcon(syncButton, FOLDER_DOWNLOAD);

		setIcon(connectSettingsItem, SERVER_SECURITY);
		setIcon(downloadSettingsItem, SETTINGS);
		setIcon(helpItem, HELP);
		setIcon(aboutAppItem, INFORMATION);
		setIcon(goToWebsiteItem, OPEN_IN_APP);
		setIcon(goToAndroidAppItem, ANDROID);
		setIcon(feedbackItem, BUG);
	}

	public void close() {
		stage.close();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

	}
}
