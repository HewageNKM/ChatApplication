package lk.ijse.chatapplication.sever;

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
    public static Server getInstance(){
         if(server==null){
              server = new Server();
         }
         return server;
    }

   private ServerSocket serverSocket;
   private final ArrayList<ClientHandler> clientHandlers;

    private Server() {
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
            ExecutorService pool = Executors.newCachedThreadPool();
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
    private void broadCastImage(BufferedImage image, String imageType, String name) {
        for (ClientHandler clientHandler : clientHandlers) {
            clientHandler.sendImage(image,imageType,name);
        }
    }

    private class ClientHandler implements Runnable {
        private final Socket client;
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
                    if(message.startsWith("<img>")){
                        handleImage(dataInputStream);
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
                throw new RuntimeException(e);
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
