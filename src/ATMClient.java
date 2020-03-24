import java.io.*;   
import java.net.*;  
import java.util.InputMismatchException;
import java.util.Scanner;
 
/**
   @author Snilledata
*/
public class ATMClient {
    private static int connectionPort = 8989;
     
    private Socket ATMSocket = null;
    private DataOutputStream out = null; //Chosen because it can handle multiple datatypes
    private DataInputStream in = null; // same
    private String adress = "";
    private Scanner scanner;
     
    public ATMClient(String args) throws IOException {
 
        try {
            adress = args;
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Missing argument ip-adress");
            System.exit(1);
        }
        try {
            ATMSocket = new Socket(adress, connectionPort); 
            out = new DataOutputStream(ATMSocket.getOutputStream());
            in = new DataInputStream(new DataInputStream
                                    (ATMSocket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Unknown host: " + adress);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't open connection to " + adress);
            System.exit(1);
        }
 
        scanner = new Scanner(System.in);
         
        System.out.println("Contacting bank ... ");
        // In the beginning of each loop it will request data from the server thread and then process it. 
        // If the input returns true then the loop will be exited.
        while(!input()) {} // Does everything !
        input(); // Good bye
        out.close();
        in.close();
        ATMSocket.close();
    }
 
    /**
     * @return true if the main loop is to be exited otherwise false
     * If input < 0 then server requests messages.
     * If input > 0 then server will send messages.
     */
    private boolean input() throws IOException {
        int input = in.readInt(); //Defines the amount of messages that will be sent to Server and Client
        if(input < 0){
            userInput(-input); //If server demands input from client
        }else{
            return readLines(input); //If the server wants to send something
        }
        return false;
    }
 
    /**
     * Reads string inputs from ServerThread.
     * Encoding is UTF-8
     */
    private String getLine() throws IOException{
        return in.readUTF();
    }
    
    /**
     * Reads the given amount of inputs of the given type (i = integers & l = longs)
     */
    private void userInput(int amount) throws IOException {
        for(int i = 0; i < amount; i++){
            char c = in.readChar();
            if(c == 'i'){
                readIntegers();
            }else if(c == 'l'){
                readLongs();
            }
        }
    }
 
    /**
     * Reads longs (Used for account numbers)
     */
    private void readLongs() throws IOException {
    	System.out.print("> ");
    	long l;
    	try{
    		l = scanner.nextLong();
    	}catch(InputMismatchException e){
    		scanner.nextLine();
    		l = 0;
    	}
   		out.writeLong(l);
    }
 
    /**
     * Reads integers (Used for everything else. Choice, Pin, amount...)
     */
    private void readIntegers() throws IOException {
    	System.out.print("> ");
    	int i;
    	try{
    		i = scanner.nextInt();
    	}catch(InputMismatchException e){
    		scanner.nextLine();
    		i = 0;
    	}
   		out.writeInt(i);
    }
 
    /**
     * If type string ('s') then printString gets called and the messages will be printed from the text document and
     * returns false.
     * If type boolean ('b') it will either return true or false.
     */
    private boolean readLines(int amount) throws IOException{
        char c = in.readChar(); 
        for(int i = 0; i < amount; i++){
            if(c == 's'){
                printString();
            }else if(c == 'b'){
                return in.readBoolean();
            }
        }
        return false;
    }
    
    /**
     * Prints the given string from getLine().
     */
    private void printString() throws IOException {
        System.out.println(getLine());
    }
    
    /**
     * Initiates the ATMClient
     */
    public static void main(String[] args) throws IOException {
        new ATMClient(args[0]);
    }
     
}