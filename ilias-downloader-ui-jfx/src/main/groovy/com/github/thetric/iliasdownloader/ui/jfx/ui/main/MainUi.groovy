package com.github.thetric.iliasdownloader.ui.jfx.ui.main

import com.github.thetric.iliasdownloader.ui.jfx.ui.util.FxmlLoaderHelper
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import groovy.transform.CompileStatic
import javafx.application.HostServices
import javafx.fxml.FXML
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import org.controlsfx.control.ToggleSwitch

import static de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon.*

/**
 * Created by Dominik Broj on 05.02.2016.
 *
 * @author Dominik Broj
 * @since 05.02.2016
 */
@CompileStatic
final class MainUi {
    private final Stage stage
    private final BorderPane rootPane

    @FXML
    private ToolBar toolBar

    @FXML
    private Button syncButton
    @FXML
    private ToggleSwitch downloadToggleSwitch

    @FXML
    private MenuButton settingsMenuButton
    @FXML
    private MenuItem connectSettingsItem, downloadSettingsItem, helpItem
    @FXML
    private MenuItem aboutAppItem, feedbackItem, goToWebsiteItem, goToAndroidAppItem
    @FXML
    private TreeTableColumn<?, ?> syncTreeTableView

    MainUi(HostServices hostServices) throws IOException {
        rootPane = (BorderPane) FxmlLoaderHelper.load(this, '/fxml/mainWindow.fxml')

        initGraphics()
        setEventHandlers(hostServices)

        final Scene scene = new Scene(rootPane)
        stage = new Stage()
        stage.icons.setAll(new Image('/icons/app-icon-64x64.png'),
                new Image('/icons/app-icon-32x32.png'), new Image('/icons/app-icon-16x16.png'))
        stage.scene = scene
        stage.title = 'Ilias Downloader 3'
        stage.show()
    }

    private static void setIcon(Labeled labeled, MaterialDesignIcon mdIcon) {
        final MaterialDesignIconView icon = new MaterialDesignIconView(mdIcon)
        labeled.setGraphic(icon)
    }

    private static void setIcon(MenuItem menuItem, MaterialDesignIcon mdIcon) {
        final MaterialDesignIconView icon = new MaterialDesignIconView(mdIcon)
        menuItem.setGraphic(icon)
    }

    private void setEventHandlers(HostServices hostServices) {
        aboutAppItem.setOnAction({
            try {
                new AboutAppDialog(hostServices).show()
            } catch (IOException e1) {
                e1.printStackTrace()
            }
        })
    }

    private void initGraphics() {
        setIcon(settingsMenuButton, SETTINGS)
        setIcon(syncButton, FOLDER_DOWNLOAD)

        setIcon(connectSettingsItem, SERVER_SECURITY)
        setIcon(downloadSettingsItem, SETTINGS)
        setIcon(helpItem, HELP)
        setIcon(aboutAppItem, INFORMATION)
        setIcon(goToWebsiteItem, OPEN_IN_APP)
        setIcon(goToAndroidAppItem, ANDROID)
        setIcon(feedbackItem, BUG)
    }

    def close() {
        stage.close()
    }
}
