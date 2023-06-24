package lk.ijse.chatapplication.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lk.ijse.chatapplication.service.impl.ChatServiceImpl;
import lk.ijse.chatapplication.service.interfaces.ChatService;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;


public class ChatFormController {
    public Button sendBtn;
    @FXML
    private AnchorPane rootPane;
    @FXML
    private VBox messageBox;
    @FXML
    private ImageView imgView;
    @FXML
    private TextField messageFld;
    private Socket client;
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;
    private String name;
    private final ChatService chatService = new ChatServiceImpl();
    private File file;

    public void initialize(String n) {
        setNameLabel(n);
        loadEmojis();
        setScrollBar();
        validateMessage();
        n=n.toUpperCase();
        this.name = n;
        new Thread(()->{
            try {
                client = new Socket("localhost",6565);
                dataOutputStream = new DataOutputStream(client.getOutputStream());
                dataOutputStream.writeUTF(name);
                dataInputStream = new DataInputStream(client.getInputStream());
                String message = dataInputStream.readUTF();
                while (!message.trim().isEmpty()){
                    message = dataInputStream.readUTF();
                    displayMessage(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void validateMessage() {
        new Thread(()->{
            while (true){
                sendBtn.setDisable(chatService.validateInput(messageFld.getText(),file));
            }
        }).start();
    }

    private void setScrollBar() {
        ScrollPane scrollPane = new ScrollPane(messageBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

    }

    private void loadEmojis() {

    }

    private void setNameLabel(String name) {
        Label nameLabel = new Label(name.toUpperCase());
        nameLabel.setStyle("-fx-text-fill: white"+";"+"-fx-font-size: 20px"+";"+"-fx-font-weight: bold"+";"+"-fx-font-family: 'Times New Roman'"+";"+"-fx-padding: 10px"+";"+"-fx-background-color: #00bfff"+";"+"-fx-background-radius: 20px");
        nameLabel.setLayoutX(159);
        nameLabel.setLayoutY(23);
        rootPane.getChildren().add(nameLabel);
    }

    void displayMessage(String message){
        Platform.runLater(()->{
            String[] mesages = message.split(":");
            if(name.equalsIgnoreCase(mesages[0])){
                Label label = new Label("Me: "+mesages[1]);
                messageBox.setSpacing(8);
                messageBox.setPadding(new javafx.geometry.Insets(10,10,10,10));
                label.setStyle("-fx-background-color: #01a64e"+";"+"-fx-background-radius: 20px"+";"+"-fx-text-fill: white"+";"+"-fx-padding: 10px");
                messageBox.getChildren().add(label);
            }else {
                Label label = new Label(message);
                messageBox.setSpacing(8);
                messageBox.setPadding(new javafx.geometry.Insets(10,10,10,10));
                label.setStyle("-fx-background-color: #00bfff"+";"+"-fx-background-radius: 20px"+";"+"-fx-text-fill: white"+";"+"-fx-padding: 10px");
                messageBox.getChildren().add(label);
            }
        });
    }
    @FXML
    private void sendAction(ActionEvent actionEvent) {
        try {
            if(chatService.validateInput(messageFld.getText(),file)){
                sendBtn.setDisable(true);
            }else {
                String message = messageFld.getText();
                chatService.sendMessage(message,client);
                messageFld.clear();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    private void attachmentAction(ActionEvent actionEvent) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Resource File");
            Stage stage = new Stage();
            file = fileChooser.showOpenDialog(stage);
            System.out.println(file.getAbsolutePath());
            chatService.sendFile(file, client);
        } catch (IOException ignored) {

        }
    }
    @FXML
    private void onMouseEnterActionImgViewer(MouseEvent mouseEvent) {
        Image image = new Image("/asset/animatedAvatar.gif");
        imgView.setImage(image);
    }
    @FXML
    private void onMouseExitActionImgViewer(MouseEvent mouseEvent) {
        Image image = new Image("/asset/avatarNotAnimated.png");
        imgView.setImage(image);
    }
    @FXML
    private void emojiAction(ActionEvent actionEvent) {
        ArrayList<String> emojis = new ArrayList<>();
        emojis.add("😀");
        Button button = new Button(emojis.get(0));
        HBox hBox = new HBox(button);
        hBox.setSpacing(10);
        hBox.setPadding(new Insets(10,10,10,10));
        rootPane.getChildren().add(hBox);
        //messageFld.appendText(emojis.get(0));
    }
}
