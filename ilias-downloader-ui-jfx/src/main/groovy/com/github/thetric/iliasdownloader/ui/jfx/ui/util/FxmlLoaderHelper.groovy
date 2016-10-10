package com.github.thetric.iliasdownloader.ui.jfx.ui.util

import groovy.transform.CompileStatic
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import lombok.experimental.UtilityClass
/**
 * Hilfsklasse, um FXML mit dem Standard-CSS und Font Awesome zu laden.
 *
 * @author Dominik Broj
 */
@UtilityClass
@CompileStatic
final class FxmlLoaderHelper {
    private static final String COMMON_CSS_URL = "css/common.css";

    /**
     * Lädt das FXML mit dem Standard-CSS und Font Awesome.
     *
     * @param ctrl
     *         Controller des FXMLs (enthält die {@link javafx.fxml.FXML} Annotations.
     * @param fxml
     *         relativer Pfad zu der FXML-Datei
     * @return die geladene Objekt-Hierarchie mit der UI
     * @throws IOException
     *         falls ein I/O-Fehler beim Laden des FXML auftrat
     * @see FxmlLoaderHelper#load(Object, String, String)
     * @see FxmlLoaderHelper#load(Object, String, String, Parent)
     */
    static Parent load(Object ctrl, String fxml) throws IOException {
        URL url = FxmlLoaderHelper.class.getResource(fxml);
        Objects.requireNonNull(url, "url");
        FXMLLoader loader = new FXMLLoader(url);
        loader.setController(ctrl);
        final Parent parent = loader.load();
        parent.getStylesheets().addAll(COMMON_CSS_URL);
        return parent;
    }

    /**
     * Wie {@link FxmlLoaderHelper#load(Object, String)}, lädt jedoch noch zusätzliches CSS.
     *
     * @param ctrl
     *         Controller des FXMLs (enthält die {@link javafx.fxml.FXML} Annotations.
     * @param fxml
     *         relativer Pfad zu der FXML-Datei
     * @param css
     *         relativer Pfad zu dem zusätzlichen CSS
     * @return die geladene Objekt-Hierarchie mit der UI
     * @throws IOException
     *         falls ein I/O-Fehler beim Laden des FXML auftrat
     * @see FxmlLoaderHelper#load(Object, String)
     * @see FxmlLoaderHelper#load(Object, String, String, Parent)
     */
    static Parent load(Object ctrl, String fxml, String css) throws IOException {
        Parent rootPane = load(ctrl, fxml);
        String styleSheet = FxmlLoaderHelper.class.getResource(css).toExternalForm();
        rootPane.getStylesheets().addAll(styleSheet);
        return rootPane;
    }

    static Parent load(Object ctrl, String fxml, String css, Parent parent) throws IOException {
        final Parent load = load(ctrl, fxml, css);
        parent.getStylesheets().addAll(COMMON_CSS_URL);
        return load;
    }
}

