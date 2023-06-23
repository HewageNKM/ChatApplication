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
import javafx.stage.Stage;
import lk.ijse.chatapplication.Client.Client;
import lk.ijse.chatapplication.service.impl.LoginServiceImpl;
import lk.ijse.chatapplication.service.interfaces.LoginService;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class LoginFormController {
    @FXML
    private TextField nameFld;
    @FXML
    private Button loginBtn;
    private ExecutorService pool = Executors.newCachedThreadPool();
    private final LoginService loginService = new LoginServiceImpl();
    public void initialize(){
        loginBtn.setTooltip(new Tooltip("Login To Chat"));
    }
    @FXML
    private void onAction(ActionEvent actionEvent) {
        if(loginService.validateName(nameFld.getText())){
            Stage stage = new Stage();
            nameFld.clear();
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
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else {
            nameFld.setStyle("-fx-border-color: red"+";"+"-fx-border-width: 2px"+";"+"-fx-border-radius: 20px");
            nameFld.setTooltip(new Tooltip("Please enter a valid name"));
        }
    }
}