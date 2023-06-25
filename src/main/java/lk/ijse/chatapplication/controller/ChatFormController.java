package lk.ijse.chatapplication.controller;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
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
import lk.ijse.chatapplication.service.ServiceFactory;
import lk.ijse.chatapplication.service.impl.ChatServiceImpl;
import lk.ijse.chatapplication.service.interfaces.ChatService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
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
    private final ChatService chatService = (ChatServiceImpl)ServiceFactory.getServiceFactory().getService(ServiceFactory.ServiceType.CHAT);
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
                String message;
                while (true){
                    message = dataInputStream.readUTF();
                    if(message.startsWith("<img>")){
                        displayImage();
                    }else {
                        displayMessage(message);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void displayImage() throws IOException {
        System.out.println("Image received");
        byte[] sizeAr = new byte[4];
        dataInputStream.readFully(sizeAr);
        int size = ByteBuffer.wrap(sizeAr).asIntBuffer().get();

        byte[] imageAr = new byte[size];
        dataInputStream.readFully(imageAr);
        String imageType = dataInputStream.readUTF();
        String sender = dataInputStream.readUTF();
        displayName(sender);
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageAr));
        Image img = SwingFXUtils.toFXImage(image, null);
        ImageView imageView = new ImageView(img);
        imageView.setStyle("-fx-border-color: #00bfff"+";"+"-fx-background-radius: 20px");
        imageView.setFitHeight(100);
        imageView.setFitWidth(100);
        Platform.runLater(()->{
            messageBox.getChildren().add(imageView);
            System.out.println("Image displayed");
        });
        imageView.setOnMouseClicked(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Image");
            fileChooser.setInitialFileName("image");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                    new FileChooser.ExtensionFilter("PNG", "*.png"),
                    new FileChooser.ExtensionFilter("GIF", "*.gif")
            );
            File file = fileChooser.showSaveDialog(new Stage());
            if (file != null) {
                try {
                    switch (imageType){
                        case "jpg":
                            ImageIO.write(SwingFXUtils.fromFXImage(img, null), "jpg", file);
                            break;
                        case "png":
                            ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png", file);
                            break;
                        case "gif":
                            ImageIO.write(SwingFXUtils.fromFXImage(img, null), "gif", file);
                            break;
                    }
                    ImageIO.write(SwingFXUtils.fromFXImage(img, null), "jpg", file);
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        });
    }

    private void displayName(String sender) {
        Platform.runLater(()->{
            Label label;
            if(sender.equalsIgnoreCase(name)){
                System.out.println(sender);
                label = new Label("Me: ");
                messageBox.setSpacing(8);
                messageBox.setPadding(new javafx.geometry.Insets(10,10,10,10));
                label.setStyle("-fx-background-color: #01a64e"+";"+"-fx-background-radius: 20px"+";"+"-fx-text-fill: white"+";"+"-fx-padding: 10px");
                messageBox.getChildren().add(label);
            }else {
                label = new Label(sender+": ");
                System.out.println(sender);
                messageBox.setSpacing(8);
                messageBox.setPadding(new javafx.geometry.Insets(10,10,10,10));
                label.setStyle("-fx-background-color: #00bfff"+";"+"-fx-background-radius: 20px"+";"+"-fx-text-fill: white"+";"+"-fx-padding: 10px");
                messageBox.getChildren().add(label);
            }
        });

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
            String[] messages = message.split(":");
            Label label;
            if(name.equalsIgnoreCase(messages[0])){
                label = new Label("Me: "+messages[1]);
                messageBox.setSpacing(8);
                messageBox.setPadding(new javafx.geometry.Insets(10,10,10,10));
                label.setStyle("-fx-background-color: #01a64e"+";"+"-fx-background-radius: 20px"+";"+"-fx-text-fill: white"+";"+"-fx-padding: 10px");
                messageBox.getChildren().add(label);
            }else {
                label = new Label(message);
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
            if(chatService.validateInput(messageFld.getText(),file)) {
                sendBtn.setDisable(true);
            }else {
                String message = messageFld.getText();
                chatService.sendMessage(message,client);
                messageFld.clear();
            }
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR,e.getLocalizedMessage()).show();
        }
    }
    @FXML
    private void attachmentAction(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll( new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
        fileChooser.setTitle("Open Resource File");
        Stage stage = new Stage();
        file = fileChooser.showOpenDialog(stage);
        try {
            chatService.sendFile(file,client, name);
        } catch (IOException | NullPointerException e) {
            System.out.println(e.getMessage());
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
