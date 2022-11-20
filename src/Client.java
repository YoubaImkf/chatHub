import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private Socket socket; // communication point through which a thread can transmit or receive information
    private BufferedWriter bufferedWriter; // Object send data to clients
    private BufferedReader bufferedReader; // Object read message have been sent from client
    private String username;

    public Client(Socket socket, String username) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = username; // set client name
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    private void sendMessage(){ // send message to clientHandler
        try{
            bufferedWriter.write(username); //send username ...
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner scanner = new Scanner(System.in); // get input from console
            while(socket.isConnected()){ // while client connected

                String msgToSend = scanner.nextLine();
                if(msgToSend.equals("QUIT")) {
                    System.out.println("exit chat...");
                    System.exit(0);
                }
                bufferedWriter.write(username + ": " + msgToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();

            }
        }catch(IOException e){
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    private void listenForMessage(){
        new Thread( new Runnable() { // we can replace with lambda () ->
            @Override
            public void run() {
                String msgFromGroupChat;

                while (socket.isConnected()) { // while connected to a client

                    try {
                        msgFromGroupChat = bufferedReader.readLine(); // read group chat messages
                        System.out.println(msgFromGroupChat);
//                        System.out.print("Enter: ");

                    } catch (IOException E) {
                        closeEverything(socket, bufferedReader, bufferedWriter);
                    }

                }
            }
        }).start();
    }

    private void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        try{
            if(bufferedReader != null){ // close read
                bufferedReader.close();
            }
            if(bufferedWriter != null){ // close Writer
                bufferedWriter.close();
            }
            if(socket != null){ // close socket
                socket.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }


    //CLIENT MAIN
    //Allow multiple instance
    public static void main(String[] args) throws IOException {

        System.out.println("-Enter: 'QUIT' to left the chat-");
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your username to join the chat: ");
        String username = scanner.nextLine();

        Socket socket = new Socket("localhost",2000);
        Client client = new Client(socket, username);
        client.listenForMessage();
        client.sendMessage();
    }
}