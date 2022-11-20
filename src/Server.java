import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private final ServerSocket serverSocket; // waits for requests to come in over the network

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    private void startServer(){
        try{
            System.out.println("Connecting...");
            while(!serverSocket.isClosed()){ // while server running execute

                Socket socket = serverSocket.accept(); // Listen for a client connection
                System.out.println("A new client has just connected");
                ClientHandler clientHandler = new ClientHandler(socket); //  clients store implements RUNNABLE

                Thread thread = new Thread(clientHandler); // Object thread
                thread.start();

            }
        }catch(IOException e){
            closeServer();
        }
    }

    private void closeServer(){ // methode that handle error
        try{
            if(serverSocket != null){
                serverSocket.close();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    //SERVER MAIN
    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(2000);
        Server server = new Server(serverSocket);
        server.startServer();

    }

}