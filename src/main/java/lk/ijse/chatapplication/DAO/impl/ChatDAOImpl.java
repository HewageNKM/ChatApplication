package lk.ijse.chatapplication.DAO.impl;

import lk.ijse.chatapplication.DAO.interfaces.ChatDAO;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;

public class ChatDAOImpl implements ChatDAO {
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    @Override
    public void sendMessage(String message, Socket client) throws IOException {
      dataOutputStream = new DataOutputStream(client.getOutputStream());
      dataOutputStream.writeUTF(message);
      dataOutputStream.flush();
    }

    @Override
    public void sendFile(File file, Socket client,String senderName) throws IOException {
        String[] fileName = file.toString().split("\\.");
        dataOutputStream = new DataOutputStream(client.getOutputStream());
        switch (fileName[1]) {
            case "jpg": {
                BufferedImage image = ImageIO.read(file);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ImageIO.write(image, "jpg", byteArrayOutputStream);

                byte[] size = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();
                dataOutputStream.writeUTF("<img>");
                dataOutputStream.write(size);
                dataOutputStream.write(byteArrayOutputStream.toByteArray());
                dataOutputStream.writeUTF("jpg");
                dataOutputStream.writeUTF(senderName);
                dataOutputStream.flush();
                System.out.println("Image sent " + file.getName());
                break;
            }
            case "png": {
                BufferedImage image = ImageIO.read(file);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ImageIO.write(image, "png", byteArrayOutputStream);

                byte[] size = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();
                dataOutputStream.writeUTF("<img>");
                dataOutputStream.write(size);
                dataOutputStream.write(byteArrayOutputStream.toByteArray());
                dataOutputStream.writeUTF("png");
                dataOutputStream.writeUTF(senderName);
                dataOutputStream.flush();
                System.out.println("Image sent " + file.getName());
                break;
            }
            case "gif": {
                BufferedImage image = ImageIO.read(file);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ImageIO.write(image, "gif", byteArrayOutputStream);

                byte[] size = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();
                dataOutputStream.writeUTF("<img>");
                dataOutputStream.write(size);
                dataOutputStream.write(byteArrayOutputStream.toByteArray());
                dataOutputStream.writeUTF("gif");
                dataOutputStream.writeUTF(senderName);
                dataOutputStream.flush();
                System.out.println("Image sent " + file.getName());
                break;
            }
        }
    }
}
