import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private final ServerSocket serverSocket;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startServer(){
        try{
            while(!serverSocket.isClosed()){ // while server running execute
                System.out.println("Connecting...");
                Socket socket = serverSocket.accept();
                System.out.println("A new client has just connected");
                ClientHandler clientHandler = new ClientHandler(socket); // store all clients

                Thread thread = new Thread(clientHandler);
                thread.start();

            }
        }catch(IOException e){
            closeServer();
        }
    }

    public void closeServer(){ // methode that handle error
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