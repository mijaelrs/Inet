import java.net.*;
import java.util.Scanner;
import java.io.*;
 
/**
   @author Viebrapadata
*/
public class ATMServer {
 
    private static int connectionPort = 8989;
    private String message = "Buy some gold from the Incas!";
     
    public ATMServer() throws IOException{
 
        ServerSocket serverSocket = null;
        
        boolean listening = true;
         
        try {
            serverSocket = new ServerSocket(connectionPort); 
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + connectionPort);
            System.exit(1);
        }
     
        System.out.println("Bank started listening on port: " + connectionPort);
         
        /**
         * Will initiate a new thread so messages can be written by the bank interactively.
         */
        new Thread(new Runnable() {         
            @Override
            public void run() {
                Scanner scanner = new Scanner(System.in);
                while(scanner.hasNext()){
                    StringBuilder sb = new StringBuilder(scanner.nextLine());
                    System.out.print("");
                    if(sb.length() > 80){
                        sb.setLength(80);
                    }
                    message = sb.toString();
                }
                scanner.close();
            }
        }).start();
         
        while (listening)
            new ATMServerThread(serverSocket.accept(), this).start();
 
        serverSocket.close();
    }
     
    public String getMessage(){
        return message;
    }
     
    public static void main(String[] args) throws IOException {
        new ATMServer();
    }
}