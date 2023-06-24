package lk.ijse.chatapplication.service.impl;

import lk.ijse.chatapplication.DAO.impl.ChatDAOImpl;
import lk.ijse.chatapplication.DAO.interfaces.ChatDAO;
import lk.ijse.chatapplication.service.interfaces.ChatService;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

public class ChatServiceImpl implements ChatService {
    private ChatDAO chatDAO = new ChatDAOImpl();
    @Override
    public void sendMessage(String message, Socket client) throws IOException {
        chatDAO.sendMessage(message,client);
    }
    @Override
    public void sendFile(File file, Socket client) throws IOException {

    }

    @Override
    public boolean validateInput(String message, File file) {
        return message.trim().isEmpty();
    }
}
