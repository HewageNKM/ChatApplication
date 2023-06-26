package lk.ijse.chatapplication.sever;

import javafx.application.Platform;
import javafx.scene.control.Alert;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable {
   private static Server server;
    private final ArrayList<ClientHandler> clientHandlers;
    public static Server getInstance(){
         if(server==null){
              server = new Server();
         }
         return server;
    }

   private Server() {
       clientHandlers = new ArrayList<>();
   }
    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(6565);
            ExecutorService pool = Executors.newCachedThreadPool();
            while(true){
                Socket client = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(client);
                pool.execute(clientHandler);
                clientHandlers.add(clientHandler);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void broadCastMessage(String message) throws IOException {
        for (ClientHandler clientHandler : clientHandlers) {
            clientHandler.sendMessage(message);
        }
    }
    private void broadCastImage(BufferedImage image, String imageType, String name) {
        for (ClientHandler clientHandler : clientHandlers) {
            clientHandler.sendImage(image,imageType,name);
        }
    }

    private class ClientHandler implements Runnable {
        private final Socket client;
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
                String name = dataInputStream.readUTF();
                broadCastMessage(name.toUpperCase()+" has joined the chat");
                while (true) {
                    String message = dataInputStream.readUTF();
                    if(message.startsWith("<img>")){
                        handleImage(dataInputStream);
                    }else if(message.equalsIgnoreCase("/exit")){
                        broadCastMessage(name.toUpperCase()+" has left the chat");
                        shutDownClient();
                        break;
                    }else {
                        broadCastMessage(name +": "+message);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private void shutDownClient() {
            try {
                clientHandlers.remove(this);
                client.close();
                dataOutputStream.close();
                dataInputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public void sendMessage(String message) throws IOException {
            dataOutputStream.writeUTF(message);
        }
        public void sendImage(BufferedImage image, String imageType, String name){
            try {
                DataOutputStream dataOutputStream = new DataOutputStream(client.getOutputStream());
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ImageIO.write(image, imageType, byteArrayOutputStream);
                byte[] size = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();

                dataOutputStream.writeUTF("<img>");
                dataOutputStream.write(size);
                dataOutputStream.write(byteArrayOutputStream.toByteArray());
                dataOutputStream.writeUTF(imageType);
                dataOutputStream.writeUTF(name);
                dataOutputStream.flush();
            } catch (IOException e) {
                Platform.runLater(()->{
                    new Alert(Alert.AlertType.ERROR,e.getLocalizedMessage()).show();
                    e.printStackTrace();
                });
            }
        }
    }

    void handleImage(DataInputStream dataInputStream) throws IOException {
        byte[] sizeAr = new byte[4];
        dataInputStream.readFully(sizeAr);
        int size = ByteBuffer.wrap(sizeAr).asIntBuffer().get();

        byte[] imageAr = new byte[size];
        dataInputStream.readFully(imageAr);
        String imageType = dataInputStream.readUTF();
        String name = dataInputStream.readUTF();
        System.out.println(name);
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageAr));

        broadCastImage(image, imageType, name);
    }

}
