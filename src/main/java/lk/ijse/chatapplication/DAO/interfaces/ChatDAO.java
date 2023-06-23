package lk.ijse.chatapplication.DAO.interfaces;

import lk.ijse.chatapplication.DAO.SuperDAO;

import java.io.IOException;
import java.net.Socket;

public interface ChatDAO extends SuperDAO {
    void sendMessage(String message, Socket client) throws IOException;
}
