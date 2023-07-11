package lk.ijse.chatapplication.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import lk.ijse.chatapplication.service.ServiceFactory;
import lk.ijse.chatapplication.service.impl.LoginServiceImpl;
import lk.ijse.chatapplication.service.interfaces.LoginService;

import java.io.IOException;



public class LoginFormController {
    @FXML
    private TextField nameFld;
    @FXML
    private Button loginBtn;
    private final LoginService loginService = (LoginServiceImpl) ServiceFactory.getServiceFactory().getService(ServiceFactory.ServiceType.LOGIN);
    public void initialize(){
        Font emojiFont = new Font("Segoe UI Emoji", 12);
        nameFld.setFont(emojiFont);
        loginBtn.setTooltip(new Tooltip("Login To Chat"));
    }
    @FXML
    private void onAction(ActionEvent actionEvent) {
        if(loginService.validateName(nameFld.getText()) && loginService.checkDuplicateName(nameFld.getText())){
            Stage stage = new Stage();
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/ChatForm.fxml"));
                Parent root = fxmlLoader.load();
                ChatFormController chatFormController = fxmlLoader.getController();
                chatFormController.initialize(nameFld.getText());
                stage.setScene(new Scene(root));
                stage.setTitle("Chat Form");
                stage.getIcons().add(new Image("/asset/message.png"));
                stage.centerOnScreen();
                stage.setResizable(false);
                stage.show();
                nameFld.clear();
                nameFld.setStyle("-fx-border-color: none");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (!loginService.checkDuplicateName(nameFld.getText())) {
            nameFld.setStyle("-fx-border-color: red"+";"+"-fx-border-width: 2px"+";"+"-fx-border-radius: 20px");
            nameFld.setTooltip(new Tooltip("Name already taken!"));
        } else {
            nameFld.setStyle("-fx-border-color: red"+";"+"-fx-border-width: 2px"+";"+"-fx-border-radius: 20px");
            nameFld.setTooltip(new Tooltip("Please enter a valid name"));
        }
    }
}
