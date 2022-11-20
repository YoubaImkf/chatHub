import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable{

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>(); // Object to Stock clients and send message to each clients
    private Socket socket;
    private BufferedWriter bufferedWriter; // Object send data to clients
    private BufferedReader bufferedReader; // Object read message have been sent from client
    private String clientUsername;

    public ClientHandler(Socket socket) {
        try{
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUsername = bufferedReader.readLine(); // set client name from keyboard (it wait client username to be sent over)
            clientHandlers.add(this); // adding the client to the list
            broadcastMessage("SERVER: " + clientUsername + " has connected to the chat.");
        }catch(IOException e){
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    @Override
    public void run() {
        String msgFromClient;

        while (socket.isConnected()){ // while connected to a client
            try{
                msgFromClient = bufferedReader.readLine(); // listen for messages
                broadcastMessage(msgFromClient); // broadcast msgFromClient
            }catch(IOException e){
                closeEverything(socket, bufferedReader, bufferedWriter);
                break; // if one client disconnect will break out the while
            }
        }
    }

    private void broadcastMessage(String msgToSend){
        for(ClientHandler clientHandler : clientHandlers){ // send message to clients ↓
            try{

                if (!clientHandler.clientUsername.equals(clientUsername)){ // except user who sent it
                    clientHandler.bufferedWriter.write(msgToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush(); // force send
                }
            }catch(IOException e){
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    private void removeFromClientHandler(){
        clientHandlers.remove( this); //remove client who's left
        broadcastMessage("SERVER: " + clientUsername + " has left the chat.");
    }

    private void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){ // close connection-streams
        removeFromClientHandler();
        try{
            if(bufferedReader != null){
                bufferedReader.close();
            }
            if(bufferedWriter != null){
                bufferedWriter.close();
            }
            if(socket != null){
                socket.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

}
