package lk.ijse.chatapplication.service.impl;

import lk.ijse.chatapplication.service.interfaces.ChatService;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

public class ChatServiceImpl implements ChatService {
    @Override
    public void sendMessage(String message, Socket client) throws IOException {
        chatDAO.sendMessage(message,client);
    }
    @Override
    public void sendFile(File file, Socket client, String name) throws IOException {
        chatDAO.sendFile(file,client,name);
    }
    @Override
    public boolean validateInput(String message, File file) {
        return message.trim().isEmpty();
    }

}
