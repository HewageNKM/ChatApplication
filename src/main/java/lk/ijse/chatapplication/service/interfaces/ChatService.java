package lk.ijse.chatapplication.service.interfaces;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

public interface ChatService {
    void sendMessage(String message, Socket client) throws IOException;
    void sendFile(File file, Socket client) throws IOException;

    boolean validateInput(String message, File file);
}
