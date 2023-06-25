package lk.ijse.chatapplication.service.interfaces;

import lk.ijse.chatapplication.DAO.DAOFactory;
import lk.ijse.chatapplication.DAO.impl.ChatDAOImpl;
import lk.ijse.chatapplication.DAO.interfaces.ChatDAO;
import lk.ijse.chatapplication.service.SuperService;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

public interface ChatService extends SuperService {
    ChatDAO chatDAO = (ChatDAOImpl) DAOFactory.getDAOFactory().getDAO(DAOFactory.DAOType.CHAT);
    void sendMessage(String message, Socket client) throws IOException;
    void sendFile(File file, Socket client, String name) throws IOException;
    boolean validateInput(String message, File file);
}
