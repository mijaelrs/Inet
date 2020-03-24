import java.io.*;
import java.net.*;
 
/**
   @author Viebrapadata
*/
public class ATMServerThread extends Thread {
    private Socket socket = null;
    private DataInputStream in;
    private DataOutputStream out;
     
    private TextHandler th;
    private String user;
    private ATMServer server;
     
    public ATMServerThread(Socket socket, ATMServer server) {
        super("ATMServerThread");
        this.socket = socket;
        th = new TextHandler();
        this.server = server;
    }
    
    /**
     * The main method.
     */
    public void run(){
        try {
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream
                (new DataInputStream(socket.getInputStream()));
             
            write(1, 's'); // Send 1 message of type string
            out.writeUTF(th.getLine(0)); // The servers welcome/tips-message
            setLanguage();
             
            int value;
            login();
            write(2, 's'); // Send 1 message of type string
            out.writeUTF(server.getMessage()); // The server's message
            out.writeUTF(th.getLine(2)); // (1)Balance, (2)Withdrawal, (3)Deposit, (4)Exit
            write(-1, 'i'); // Receive 1 message of type integer
            int choice = readInteger(); // Their input
            while (choice != 4) {
                int deposit = 1;
                switch (choice) {
                case 2:
                    deposit = -1;
                case 3:
                    if(choice == 2){ // They are withdrawing
                        write(1, 's'); // Write 1 message of type string
                        out.writeUTF(th.getLine(12)); // Enter your one time code:
                        write(-1, 'i'); // Receive 1 message of type integer
                        int code = readInteger(); // Their input
                        if(code % 2 != 0 && code > 0 && code < 100){ // Only number that can be divided by 2 
                            write(1, 's'); // Write 1 message of type string
                            out.writeUTF(th.getLine(11)); // Code accepted
                            write(1, 's'); // Write 1 message of type string
                            out.writeUTF(th.getLine(3)); // Enter amount:
                            write(-1, 'i'); // Receive 1 message of type integer
                            value = Math.abs(readInteger()); // Their input
                            if(AccountHandler.getBalance(user) + (deposit*value) < 0){
                                write(1, 's'); // Send 1 message of type string
                                out.writeUTF(th.getLine(4)); // Tell the client that they failed to withdraw money
                            }else{
                                AccountHandler.setBalance(user, "" + (AccountHandler.getBalance(user) + (deposit * value))); // Set the user's balance to the new value
                            }
                        }else{
                            write(1, 's'); // Write 1 message of type string
                            out.writeUTF(th.getLine(13)); // Code not accepted
                        }
                    }else{ // They wish to deposit
                        write(1, 's'); // Write 1 message of type string
                        out.writeUTF(th.getLine(3)); // Enter amount:
                        write(-1, 'i'); // Receive 1 message of type integer
                        value = Math.abs(readInteger()); // Their input
                        AccountHandler.setBalance(user, "" + (AccountHandler.getBalance(user) + (deposit * value))); // Set the user's balance to the new value
                    }
                case 1:
                    write(3, 's'); // Send 2 message of type string
                    out.writeUTF(th.getLine(9) + AccountHandler.getBalance(user) + " dollars"); // Tell the user their new balance
                    out.writeUTF(server.getMessage()); // The server's message
                    out.writeUTF(th.getLine(2)); // (1)Balance, (2)Withdrawal, (3)Deposit, (4)Exit
                    write(-1, 'i'); // Receive 1 message of type integer
                    choice = readInteger(); // Read their input
                    break;
                default: 
                    write(3, 's'); // Send 2 messages of type string
                    out.writeUTF(th.getLine(14) + "4"); // The input must be between 1 and '4'
                    out.writeUTF(server.getMessage()); // The server's message
                    out.writeUTF(th.getLine(2)); // (1)Balance, (2)Withdrawal, (3)Deposit, (4)Exit
                    write(-1, 'i'); // Receive 1 message of type integer
                    choice = readInteger(); // Get user input
                    break;
                }
            }
            write(1, 'b'); // Send 1 message of type boolean
            out.writeBoolean(true); // Tell the client to exit the loop
            write(1, 's'); // Send 1 message of type string
            out.writeUTF(th.getLine(10)); // Good bye
            out.close();
            in.close();
            socket.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    /**
     * Asks the client what language it prefers. After choice it sends back an integer between 1-5 to Server and sets it to that
     * language. If the choice was not a valid index then it sends a message with the alternatives again.
     */
    private void setLanguage() throws IOException{
        while(true){
	    	write(th.getLanguages().length+1, 's'); // Send the amount of languages + 1 of type string
	        out.writeUTF(th.getLine(1)); // Select your language
	        String[] temp = th.getLanguages(); // Get a list of all the languages
	        for(int i = 1; i <= temp.length; i++){
	            out.writeUTF("(" + i + ")" + temp[i-1]); // All the languages
	        }
	        write(-1, 'i'); // Receive 1 message of type integer
	        int input = readInteger(); // Read the input
	        input--;
        	if(input < 0 || input > temp.length){ // If bad input do this
        		write(1, 's'); // Send 1 message of type string
	        	out.writeUTF(th.getLine(14) + temp.length); // The input must be between 1 and 'the amount of languages'
        	}else{
        		th.setLanguage(input); // Set the language
        		break; // break the loop, the input was accepted
        	}
        }
    }
 
    /**
     * Reads the account number and the pin code and evaluates whether the user exists or not.
     */
    private void login() throws IOException {
        while(true){
            write(1, 's'); // Send 1 message of type string
            out.writeUTF(th.getLine(6)); // Enter account number: 
            StringBuilder sb = new StringBuilder();
             
            write(-1, 'l'); // Receive 1 message of type long
            sb.append(readLong());
            write(1, 's'); // Send 1 message of type string
            out.writeUTF(th.getLine(7)); // Enter pin code:
            write(-1, 'i'); // Receive 1 message of type integer
            sb.append(" " + readInteger());
            if(AccountHandler.userExists(sb.toString())){
                user = sb.toString();
                write(1, 's'); // Send 1 message of type string
                out.writeUTF(th.getLine(11)); // Code accepted
                break;
            }
            write(1, 's'); // Send 1 message of type string
            out.writeUTF(th.getLine(8)); // Code NOT accepted
        }
    }
    /**
     *  Reads everything else (Choice, balance, pin....)
     */
    private int readInteger() throws IOException {
        return in.readInt();
    }
     
    /**
     * Reads the account number.
     */
    private long readLong() throws IOException {
        return in.readLong();
    }
 
    /**
     * Matches the parameters in the methods so the server can decide to either request or send information by recognizing
     * the amount of messages and the type (being either a booleans or string).
     */
    private void write(int amount, char type) throws IOException {
        out.writeInt(amount);
        out.writeChar(type);
    }
}