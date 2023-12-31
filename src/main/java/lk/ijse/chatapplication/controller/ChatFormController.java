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
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
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
    public ScrollPane emojiPane;
    @FXML
    private Label countLabel;
    @FXML
    private Button sendBtn;
    @FXML
    private VBox emojiBox;
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
    private Thread thread;
    private boolean done = false;

    public void initialize(String n) {
        emojiPane.setVisible(false);
        setNameLabel(n);
        loadEmojis();
        setScrollBar();
        validateMessage();
        n = n.toUpperCase();
        setCountLabel();
        Font emojiFont = new Font("Noto Color Emoji", 12);
        messageFld.setFont(emojiFont);
        this.name = n;
         thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    client = new Socket("localhost",6565);
                    dataOutputStream = new DataOutputStream(client.getOutputStream());
                    dataOutputStream.writeUTF(name);
                    dataInputStream = new DataInputStream(client.getInputStream());
                    while (!done){
                       String message = dataInputStream.readUTF();
                        if(message.startsWith("<img>")){
                            displayImage();
                        }else {
                            displayMessage(message);
                        }
                    }
                } catch (IOException e) {
                    System.out.println(e.getLocalizedMessage());
                }
            }
        });
        thread.start();
    }

    private void setCountLabel() {
        new Thread(()->{
            while (true){
               if(messageFld.getText().isEmpty()){
                     Platform.runLater(()->{
                          countLabel.setText("0/100");
                     });
               }else {
                   Platform.runLater(() -> {
                       countLabel.setText(messageFld.getText().length() + "/100");
                       messageFld.setDisable(messageFld.getText().length() >= 100);
                   });
               }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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
                    Platform.runLater(()->{
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText("Error");
                        alert.setContentText("Image not saved");
                        alert.show();
                    });
                }
            }
        });
    }

    private void displayName(String sender) {
        Platform.runLater(()->{
            Label label;
            if(sender.equalsIgnoreCase(name)){
                label = new Label("Me: ");
                messageBox.setSpacing(8);
                messageBox.setPadding(new javafx.geometry.Insets(10,10,10,10));
                label.setStyle("-fx-background-color: #01a64e"+";"+"-fx-background-radius: 20px"+";"+"-fx-text-fill: white"+";"+"-fx-padding: 10px"+";"+"-fx-font-family: 'Noto Emoji', sans-serif"+";"+"-fx-font-weight: bold"+";"+"-fx-font-size: 15px");
                messageBox.getChildren().add(label);
            }else {
                label = new Label(sender+": ");
                messageBox.setSpacing(8);
                messageBox.setPadding(new javafx.geometry.Insets(10,10,10,10));
                label.setStyle("-fx-background-color: #00bfff"+";"+"-fx-background-radius: 20px"+";"+"-fx-text-fill: white"+";"+"-fx-padding: 10px"+";"+"-fx-font-family: 'Noto Emoji', sans-serif"+";"+"-fx-font-weight: bold"+";"+"-fx-font-size: 15px");
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
        ArrayList<String> emojis = new ArrayList<>();
        emojis.add("😀");
        emojis.add("\uD83D\uDE42");
        emojis.add("\uD83D\uDE44");
        emojis.add("\uD83D\uDE19");
        emojis.add("\uD83D\uDE10");
        emojis.add("\uD83D\uDE34");
        emojis.add("\uD83D\uDE37");
        emojis.add("\uD83D\uDE21");
        emojiBox.setPadding(new javafx.geometry.Insets(10,10,10,10));
        emojiBox.setSpacing(10);
        Font font = Font.font("Noto Emoji", FontWeight.BOLD, 20);
        for (String emoji:emojis){
            Label btn = new Label("<html>&#128516;</html>");
            btn.setFont(font);
            btn.setStyle("-fx-font-size: 20px");
            btn.setOnMouseClicked(event -> {
                messageFld.setText(messageFld.getText()+emoji);
            });
            emojiBox.getChildren().add(btn);
        }

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
                label.setStyle("-fx-background-color: #01a64e"+";"+"-fx-background-radius: 20px"+";"+"-fx-text-fill: white"+";"+"-fx-padding: 10px"+";"+"-fx-font-family: 'Noto Emoji', sans-serif"+";"+"-fx-font-weight: bold"+";"+"-fx-font-size: 15px");
                messageBox.getChildren().add(label);
            }else {
                label = new Label(message);
                messageBox.setSpacing(8);
                messageBox.setPadding(new javafx.geometry.Insets(10,10,10,10));
                label.setStyle("-fx-background-color: #00bfff"+";"+"-fx-background-radius: 20px"+";"+"-fx-text-fill: white"+";"+"-fx-padding: 10px"+";"+"-fx-font-family: 'Noto Emoji', sans-serif"+";"+"-fx-font-weight: bold"+";"+"-fx-font-size: 15px");
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
                if(message.startsWith("/Exit") || message.startsWith("/exit")){
                    chatService.sendMessage(message,client);
                    quit();
                }else {
                    chatService.sendMessage(message,client);
                    messageFld.clear();
                }

            }
        } catch (IOException e) {
            Platform.runLater(()->{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error sending message");
                alert.setContentText("Error sending message");
                alert.showAndWait();
            });
        }
        messageFld.setDisable(false);
    }

    private void quit() {
        try {
            dataInputStream.close();
            dataOutputStream.close();
            client.close();
            done = true;
            thread.interrupt();
            Platform.runLater(()->{
                Stage stage = (Stage) rootPane.getScene().getWindow();
                stage.close();
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
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
    private void OnMouseEnterEmojiBox(MouseEvent mouseEvent) {
        emojiPane.setVisible(true);
    }

    public void emojiBtnAction(ActionEvent actionEvent) {
        emojiPane.setVisible(true);
    }

    public void bgClickedAction(MouseEvent mouseEvent) {
        emojiPane.setVisible(false);
    }
}
