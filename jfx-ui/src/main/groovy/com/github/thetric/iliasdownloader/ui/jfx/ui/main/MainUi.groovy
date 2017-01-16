package com.github.thetric.iliasdownloader.ui.jfx.ui.main

import com.github.thetric.iliasdownloader.service.IliasService
import com.github.thetric.iliasdownloader.service.SyncingIliasItemVisitor
import com.github.thetric.iliasdownloader.service.model.Course
import com.github.thetric.iliasdownloader.service.model.CourseFile
import com.github.thetric.iliasdownloader.ui.jfx.prefs.UserPreferences
import com.github.thetric.iliasdownloader.ui.jfx.ui.util.FxmlLoaderHelper
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import groovy.transform.Canonical
import groovy.transform.CompileStatic
import groovy.util.logging.Log4j2
import io.reactivex.functions.Consumer
import javafx.application.HostServices
import javafx.application.Platform
import javafx.beans.property.ReadOnlyStringWrapper
import javafx.beans.value.ObservableValue
import javafx.fxml.FXML
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import javafx.util.Callback
import org.controlsfx.control.ToggleSwitch

import java.nio.file.Path
import java.nio.file.Paths

import static de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon.*

/**
 * Created by Dominik Broj on 05.02.2016.
 *
 * @author Dominik Broj
 * @since 05.02.2016
 */
@CompileStatic
@Log4j2
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
    private MenuItem aboutAppItem

    @FXML
    private TableView<ItemTableModel> itemTable
    @FXML
    private TableColumn<ItemTableModel, String> itemNameCol, itemSizeCol

    private final UserPreferences userPreferences

    @Canonical
    private static final class ItemTableModel {
        CourseFile courseFile
        Path path
    }

    MainUi(HostServices hostServices, IliasService iliasService, UserPreferences userPreferences) throws IOException {
        this.userPreferences = userPreferences
        rootPane = (BorderPane) FxmlLoaderHelper.load(this, '/fxml/mainWindow.fxml')

        initGraphics()
        setEventHandlers(hostServices, iliasService)

        itemNameCol.cellValueFactory = new Callback<TableColumn.CellDataFeatures<ItemTableModel, String>, ObservableValue<String>>() {
            ObservableValue<String> call(TableColumn.CellDataFeatures<ItemTableModel, String> p) {
                new ReadOnlyStringWrapper(p.value.courseFile.name)
            }
        }

        itemTable.rowFactory = {
            def row = new TableRow<ItemTableModel>()
            row.onMouseClicked = {
                if (it.clickCount == 2 && !row.empty) {
                    hostServices.showDocument(row.item.path.toUri().toURL().toExternalForm())
                }
            }
            return row
        }

        final Scene scene = new Scene(rootPane)
        stage = new Stage()
        stage.icons.setAll(
                new Image('/icons/app-icon-64x64.png'),
                new Image('/icons/app-icon-32x32.png'),
                new Image('/icons/app-icon-16x16.png'))
        stage.scene = scene
        stage.title = 'Ilias Downloader 3'
        stage.show()
    }

    private static void setIcon(Labeled labeled, MaterialDesignIcon mdIcon) {
        final MaterialDesignIconView icon = new MaterialDesignIconView(mdIcon)
        labeled.graphic = icon
    }

    private static void setIcon(MenuItem menuItem, MaterialDesignIcon mdIcon) {
        final MaterialDesignIconView icon = new MaterialDesignIconView(mdIcon)
        menuItem.graphic = icon
    }

    private void setEventHandlers(HostServices hostServices, IliasService iliasService) {
        aboutAppItem.onAction = {
            try {
                new AboutAppDialog(hostServices).show()
            } catch (IOException e1) {
                log.error(e1)
            }
        }
        syncButton.onAction = {
            syncButton.disable = true
            itemTable.items.clear()
            Thread.start {
                log.info 'running sync'
                try {
                    def courses = iliasService.joinedCourses
                    def basePath = Paths.get userPreferences.downloadFolder
                    def itemVisitor = new ItemTableUpdater(basePath, iliasService, this)
                    iliasService.searchCoursesWithContent(courses).subscribe(new Consumer<Course>() {
                        @Override
                        void accept(Course course) {
                            itemVisitor.visit course
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        void accept(Throwable throwable) {
                            log.error throwable
                        }
                    })
                } catch (Throwable throwable) {
                    log.error throwable
                } finally {
                    Platform.runLater({ syncButton.disable = false })
                }
                log.info 'sync finished'
            }
        }
    }

    private static final class ItemTableUpdater extends SyncingIliasItemVisitor {
        private MainUi mainUi

        ItemTableUpdater(Path basePath, IliasService iliasService, MainUi mainUi) {
            super(basePath, iliasService)
            this.mainUi = mainUi
        }

        @Override
        void syncAndSaveFile(Path path, CourseFile file) {
            Platform.runLater({
                mainUi.itemTable.items << new ItemTableModel(file, path)
            })
            super.syncAndSaveFile(path, file)
        }
    }

    private void initGraphics() {
        setIcon(settingsMenuButton, SETTINGS)
        setIcon(syncButton, FOLDER_DOWNLOAD)

        setIcon(connectSettingsItem, SERVER_SECURITY)
        setIcon(downloadSettingsItem, SETTINGS)
        setIcon(aboutAppItem, INFORMATION)
    }

    def close() {
        stage.close()
    }
}
