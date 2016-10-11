package com.github.thetric.iliasdownloader.ui.jfx.ui.main

import com.github.thetric.iliasdownloader.ui.jfx.ui.util.FxmlLoaderHelper
import groovy.transform.CompileStatic
import javafx.application.HostServices
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.concurrent.Worker
import javafx.fxml.FXML
import javafx.scene.control.ButtonType
import javafx.scene.control.Dialog
import javafx.scene.web.WebView
import org.w3c.dom.Document
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import org.w3c.dom.events.EventTarget
import org.w3c.dom.html.HTMLAnchorElement

import static javafx.concurrent.Worker.State.SUCCEEDED
import static javafx.scene.control.ButtonType.CLOSE
/**
 * Created by Dominik Broj on 05.02.2016.
 *
 * @author Dominik Broj
 * @since 05.02.2016
 */
@CompileStatic
final class AboutAppDialog extends Dialog<ButtonType> {
    @FXML
    private WebView aboutAppWebView

    AboutAppDialog(HostServices hostServices) throws IOException {
        title = 'Über diese Anwendung'
        headerText = 'Über Ilias Downloader 3'
        getDialogPane().buttonTypes.all = CLOSE

        FxmlLoaderHelper.load(this, '/fxml/aboutApp.fxml')

        def aboutApp = AboutAppDialog.getResource('/html/aboutApp.html').toExternalForm()
        def engine = aboutAppWebView.engine
        engine.loadWorker.stateProperty().addListener(new ChangeListener<Worker.State>() {
            @Override
            void changed(ObservableValue<? extends Worker.State> observable, Worker.State oldValue, Worker.State newValue) {
                if (newValue == SUCCEEDED) {
                    setupOpenAllLinksInSystemBrowser engine.document, hostServices
                }
            }
        })
        engine.load aboutApp

        getDialogPane().content = aboutAppWebView
    }

    def setupOpenAllLinksInSystemBrowser(Document document, HostServices hostServices) {
        NodeList nodeList = document.getElementsByTagName('a')
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i)
            EventTarget eventTarget = (EventTarget) node
            eventTarget.addEventListener('click', {
                it.preventDefault()
                EventTarget target = it.currentTarget
                HTMLAnchorElement anchorElement = target as HTMLAnchorElement
                String url = anchorElement.href
                hostServices.showDocument url
            }, false)
        }
    }
}
