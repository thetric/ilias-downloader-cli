package com.github.thetric.iliasdownloader.ui.jfx.ui.intro.setup

import com.github.thetric.iliasdownloader.ui.jfx.prefs.UserPreferences
import com.github.thetric.iliasdownloader.ui.jfx.ui.settings.SyncFolderChooserController
import groovy.transform.CompileStatic
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType

import java.nio.file.Files
import java.nio.file.Paths

import static com.github.thetric.iliasdownloader.ui.jfx.ui.util.DialogHelper.showBigDialog
import static javafx.scene.control.Alert.AlertType.*

/**
 * Checks if the {@link UserPreferences} are invalid and shows dialogs to correct them.
 */
@CompileStatic
final class SettingsValidator {
    void validateSettings(UserPreferences userPreferences, Runnable nextAction) {
        if (!userPreferences.downloadFolder) {
            new Alert(INFORMATION, 'Lege bitte den Speicherordner für den Sync fest', ButtonType.NEXT)
                    .showAndWait()
                    .ifPresent({ showSyncFolderDialog(userPreferences, nextAction) })
        } else {
            def downloadFolder = Paths.get(userPreferences.downloadFolder)
            if (!Files.exists(downloadFolder)) {
                showBigDialog(WARNING, "Der Speicherordner ${downloadFolder.toAbsolutePath()} " +
                        "kann nicht gefunden. Wähle bitte einen anderen Ordner aus.")
                        .showAndWait()
                        .ifPresent({ showSyncFolderDialog(userPreferences, nextAction) })
            } else if (!Files.isWritable(downloadFolder)) {
                showBigDialog(WARNING, "Der Speicherordner ${downloadFolder.toAbsolutePath()} " +
                        "kann nicht zum Schreiben geöffnet werden. Wähle bitte einen anderen Ordner aus.")
                        .showAndWait()
                        .ifPresent({ showSyncFolderDialog(userPreferences, nextAction) })
            } else {
                nextAction.run()
            }
        }
    }

    private void showSyncFolderDialog(UserPreferences userPreferences, Runnable afterFolderChosenCallback) {
        new SyncFolderChooserController().getPath().ifPresent({
            if (!Files.isWritable(it)) {
                def alert = showBigDialog(ERROR, "Für den angegebenen Ordner ${it.toAbsolutePath()} fehlen " +
                        "Schreibrechte. Bitte prüfe die Ordnerrechte oder wähle einen anderen Ordner aus")
                alert.buttonTypes.addAll(ButtonType.OK, ButtonType.CANCEL)
                alert.showAndWait()
                     .filter({ it == ButtonType.OK })
                     .ifPresent({ showSyncFolderDialog(userPreferences, afterFolderChosenCallback) })
            } else {
                userPreferences.downloadFolder = it
                afterFolderChosenCallback.run()
            }
        })
    }
}
