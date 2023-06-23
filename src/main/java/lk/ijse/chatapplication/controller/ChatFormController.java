package lk.ijse.chatapplication.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import lk.ijse.chatapplication.Client.Client;
import lk.ijse.chatapplication.service.impl.ChatServiceImpl;
import lk.ijse.chatapplication.service.interfaces.ChatService;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ChatFormController {
    @FXML
    private TextArea textArea;
    @FXML
    private TextField messageFld;
    private Socket client;
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;
    private String name;
    private AnchorPane chatPane;
    private final ChatService chatService = new ChatServiceImpl();

    public void initialize(String n){
        name = n;
        new Thread(()->{
            try {
                client = new Socket("localhost",6565);
                dataOutputStream = new DataOutputStream(client.getOutputStream());
                dataOutputStream.writeUTF(name);
                dataInputStream = new DataInputStream(client.getInputStream());
                String message = "";
                while (message!=null){
                    message = dataInputStream.readUTF();
                    textArea.appendText(message+"\n");
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    @FXML
    private void sendAction(ActionEvent actionEvent) {
        try {
            String message = messageFld.getText();
            chatService.sendMessage(message,client);
            messageFld.clear();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    private void attachmentAction(ActionEvent actionEvent) {

    }

}
