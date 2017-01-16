package com.github.thetric.iliasdownloader.ui.jfx.ui.settings

import javafx.stage.DirectoryChooser
import javafx.stage.Window

import java.nio.file.Path

/**
 * Shows a dialog asking the user to select a directory for the sync folder.
 */
final class SyncFolderChooserController {

    Optional<Path> getPath(Window parent = null) {
        def dirChooser = new DirectoryChooser()
        dirChooser.title = 'Zielverzeichnis zur Synchronisation'

        def userHomeDir = System.getProperty('user.home')
        if (userHomeDir) {
            dirChooser.initialDirectory = new File(userHomeDir)
        }

        return Optional.ofNullable(dirChooser.showDialog(parent))
                       .map({ it.toPath() })
    }
}
