package lk.ijse.chatapplication.Client;


import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client implements Runnable{
    private Socket client;
    private String name;
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;
    public Client(String name){
        this.name=name;
    }
    @Override
    public void run() {
        try {
            client = new Socket("localhost",6565);
            dataOutputStream = new DataOutputStream(client.getOutputStream());
            dataOutputStream.writeUTF(name);
            dataInputStream = new DataInputStream(client.getInputStream());
            new Thread(new MessageHandler()).start();
            while (true){
                String message = dataInputStream.readUTF();
                System.out.println(message);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private class MessageHandler implements Runnable{
        @Override
        public void run() {
            try {
                while (true){
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
                    String message = bufferedReader.readLine();
                    dataOutputStream.writeUTF(message);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public static void main(String[] args) {
        String clientName;
        while (true){
            Scanner  scanner = new Scanner(System.in);
            System.out.println("Type 'exit' to exit the chat application");
            System.out.print("Enter your Name: ");
            clientName = scanner.nextLine();
            if (clientName.equalsIgnoreCase("exit")){
                System.exit(0);
            }else {
                break;
            }
        }
        new Client(clientName).run();
    }
}
