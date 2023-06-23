package lk.ijse.chatapplication.DAO.impl;

import lk.ijse.chatapplication.DAO.interfaces.ChatDAO;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ChatDAOImpl implements ChatDAO {
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    @Override
    public void sendMessage(String message, Socket client) throws IOException {
      dataOutputStream = new DataOutputStream(client.getOutputStream());
      dataOutputStream.writeUTF(message);
      dataOutputStream.flush();
    }
}
