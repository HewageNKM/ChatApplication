package lk.ijse.chatapplication.service.interfaces;

import java.io.IOException;
import java.net.Socket;

public interface ChatService {
    void sendMessage(String message, Socket client) throws IOException;
}
