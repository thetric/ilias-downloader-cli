package com.github.thetric.iliasdownloader.ui.jfx.ui.util;

import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.Pair;
import org.controlsfx.control.textfield.CustomPasswordField;
import org.controlsfx.control.textfield.CustomTextField;
import org.controlsfx.control.textfield.TextFields;
import org.controlsfx.dialog.LoginDialog;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import static impl.org.controlsfx.i18n.Localization.getString;
import static javafx.event.ActionEvent.ACTION;

public final class PreValidatingDialog extends Dialog<Pair<String, String>> {

    private final ButtonType loginButtonType;
    private final CustomTextField txUserName;
    private final CustomPasswordField txPassword;

    public PreValidatingDialog(final Pair<String, String> initialUserInfo, final Callback<Pair<String, String>, Void> authenticator) {
        final DialogPane dialogPane = getDialogPane();

        setTitle(getString("login.dlg.title")); //$NON-NLS-1$
        dialogPane.setHeaderText(getString("login.dlg.header")); //$NON-NLS-1$
        dialogPane.getStyleClass().add("login-dialog"); //$NON-NLS-1$
        dialogPane.getStylesheets().add(LoginDialog.class.getResource("dialogs.css").toExternalForm()); //$NON-NLS-1$
        dialogPane.getButtonTypes().addAll(ButtonType.CANCEL);


        txUserName = (CustomTextField) TextFields.createClearableTextField();

        txUserName.setLeft(new ImageView(LoginDialog.class.getResource("/org/controlsfx/dialog/user.png")
                                                          .toExternalForm())); //$NON-NLS-1$

        txPassword = (CustomPasswordField) TextFields.createClearablePasswordField();
        txPassword.setLeft(new ImageView(LoginDialog.class.getResource("/org/controlsfx/dialog/lock.png")
                                                          .toExternalForm())); //$NON-NLS-1$

        Label lbMessage = new Label("");  //$NON-NLS-1$
        lbMessage.getStyleClass().addAll("message-banner"); //$NON-NLS-1$
        lbMessage.setVisible(false);
        lbMessage.setManaged(false);

        final VBox content = new VBox(10);
        content.getChildren().add(lbMessage);
        content.getChildren().add(txUserName);
        content.getChildren().add(txPassword);

        dialogPane.setContent(content);

        loginButtonType = new javafx.scene.control.ButtonType(getString("login.dlg.login.button"),
                                                              ButtonData.OK_DONE); //$NON-NLS-1$
        dialogPane.getButtonTypes().addAll(loginButtonType);
        Button loginButton = (Button) dialogPane.lookupButton(loginButtonType);
        loginButton.addEventFilter(ACTION, event -> {
            try {
                if (authenticator != null) {
                    authenticator.call(new Pair<>(txUserName.getText(), txPassword.getText()));
                }
                lbMessage.setVisible(false);
                lbMessage.setManaged(false);
            } catch (Throwable ex) {
                event.consume();
                lbMessage.setVisible(true);
                lbMessage.setManaged(true);
                lbMessage.setText(ex.getMessage());
            }
        });

        String userNameCation = getString("login.dlg.user.caption"); //$NON-NLS-1$
        String passwordCaption = getString("login.dlg.pswd.caption"); //$NON-NLS-1$
        txUserName.setPromptText(userNameCation);
        txUserName.setText(initialUserInfo == null ? "" : initialUserInfo.getKey()); //$NON-NLS-1$
        txPassword.setPromptText(passwordCaption);
        txPassword.setText(initialUserInfo == null ? "" : initialUserInfo.getValue()); //$NON-NLS-1$

        ValidationSupport validationSupport = new ValidationSupport();
        Platform.runLater(() -> {
            String requiredFormat = "'%s' is required"; //$NON-NLS-1$
            validationSupport.registerValidator(txUserName,
                                                Validator.createEmptyValidator(String.format(requiredFormat,
                                                                                             userNameCation)));
            validationSupport.registerValidator(txPassword,
                                                Validator.createEmptyValidator(String.format(requiredFormat,
                                                                                             passwordCaption)));
            txUserName.requestFocus();
        });


        setResultConverter(dialogButton -> dialogButton == loginButtonType ?
                new Pair<>(txUserName.getText(), txPassword.getText()) : null);
    }

}
