package lk.ijse.chatapplication.sever;

import javafx.fxml.Initializable;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable {
   private ServerSocket serverSocket;
   private ArrayList<ClientHandler> clientHandlers;
   private ExecutorService pool;
   public Server() {
       clientHandlers = new ArrayList<>();
   }
   private void shutDown(){
         try {
             if (!serverSocket.isClosed()){
                 serverSocket.close();
             }
         } catch (IOException e) {
             throw new RuntimeException(e);
         }
   }
    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(6565);
            pool = Executors.newCachedThreadPool();
            System.out.println("Server is waiting for clients");
            while(true){
                Socket client = serverSocket.accept();
                System.out.println("Client Request Accepted");
                ClientHandler clientHandler = new ClientHandler(client);
                pool.execute(clientHandler);
                clientHandlers.add(clientHandler);
            }
        } catch (IOException e) {
            shutDown();
            throw new RuntimeException(e);
        }
    }

    void broadCastMessage(String message) throws IOException {
        for (ClientHandler clientHandler : clientHandlers) {
            clientHandler.sendMessage(message);
        }
    }

    private class ClientHandler implements Runnable {
        private Socket client;
        private String name;
        private DataInputStream dataInputStream;
        private DataOutputStream dataOutputStream;

        public ClientHandler(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            try {
                dataInputStream = new DataInputStream(client.getInputStream());
                dataOutputStream = new DataOutputStream(client.getOutputStream());
                name = dataInputStream.readUTF();
                broadCastMessage(name.toUpperCase()+" has joined the chat");
                while (true) {
                    String message = dataInputStream.readUTF();
                    if (message.startsWith("/e") || message.startsWith("/E")) {
                        broadCastMessage(name.toUpperCase()+" has left the chat");
                        dataOutputStream.close();
                        dataInputStream.close();
                        client.close();
                        clientHandlers.remove(this);
                        break;
                    }else if (message.startsWith("/n") || message.startsWith("/N")){
                        String[] split = message.split(" ");
                        String newName = split[1];
                        broadCastMessage(name.toUpperCase()+" has changed the name to "+newName.toUpperCase());
                        name = newName;
                    }else if (message.startsWith("/l") || message.startsWith("/L")){
                        broadCastMessage("List of users");
                        for (ClientHandler clientHandler : clientHandlers) {
                            broadCastMessage(clientHandler.name);
                        }
                    }else {
                        broadCastMessage(name+": "+message);
                    }
                }
            } catch (IOException e) {
                try {
                    client.close();
                    shutDown();
                    dataOutputStream.close();
                    dataInputStream.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                throw new RuntimeException(e);
            }
        }

        public void sendMessage(String message) throws IOException {
            dataOutputStream.writeUTF(message);
        }
    }
    public static void main(String[] args) {
        Server server = new Server();
        server.run();
    }
}
