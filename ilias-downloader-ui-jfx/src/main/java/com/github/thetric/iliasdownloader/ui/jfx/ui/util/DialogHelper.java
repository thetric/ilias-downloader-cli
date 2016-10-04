package com.github.thetric.iliasdownloader.ui.jfx.ui.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

/**
 * @author broj
 * @since 25.09.2016
 */
@Log4j2
public final class DialogHelper {

    private DialogHelper() {
        // prevent instantiation
        throw new IllegalAccessError();
    }

    public static Optional<ButtonType> showExceptionDialog(String message, Throwable throwable) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ilias Downloader 3 - Fehler");
        alert.setHeaderText(message);
        alert.setContentText(throwable.getLocalizedMessage());

        TextArea exceptionLogTextArea = new TextArea(getPrintableString(throwable));
        exceptionLogTextArea.setStyle("-fx-font-family: Hack, Consolas, 'Courier New', monospace;");
        alert.getDialogPane().setExpandableContent(exceptionLogTextArea);

        return alert.showAndWait();
    }

    private static String getPrintableString(Throwable throwable) {
        try(StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw)) {
            throwable.printStackTrace(pw);
            return sw.toString();
        } catch (IOException e) {
            // can't happen
            log.error("Could not close the StringWriter", e);
        }
        return "<Fehler beim Erzeugen des Exception Stacktraces<";
    }
}
