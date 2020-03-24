import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
 
public class AccountHandler {
     
    /**
     * Reads from the accounts.txt and will evaluate if the input matches the account listed in the text file.
     * @return true if user exists.
     */
	public static boolean userExists(String user){
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("accounts.txt"), "UTF-8"));
            String line;
            while ((line = br.readLine()) != null) {
                if(line.matches(user)){
                    br.close();
                    return true;
                }
            }
            br.close();
        } catch (IOException e) {
            System.err.println("Corrupt file \"accounts\"");
            System.exit(1);
        }
        return false;
    }
     
   /**
    * If the user is found then return balance otherwise it returns "-1"
    */
	public static int getBalance(String user){
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("accounts.txt"), "UTF-8"));
            String line;
            boolean userFound = false;
            while ((line = br.readLine()) != null) {
                if(userFound){
                    br.close();
                    return Integer.parseInt(line);
                //Will try to find user first.
                }else if(line.matches(user)){
                    userFound = true;
                }
            }
            br.close();
        }catch(IOException e){
            System.err.println("Corrupt file \"accounts\"");
            System.exit(1);
        }
        return -1;
    }
 
    /**
     * If the given user is found, then it will set its balance to the given value.
     */
	public static void setBalance(String user, String balance){
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("accounts.txt"), "UTF-8"));
            String line;
            ArrayList<String> lines = new ArrayList<String>();
            //Reads in the lines of accounts.txt and saves it in the list "lines".
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
            br.close();
            for(int i = 0; i < lines.size(); i++){
                if(lines.get(i).matches(user)){
                    //Will set the next line to the new balance
                	lines.set(i+1, balance);
                    break;
                }
            }
            //The altered data will be saved at accounts.txt
            PrintWriter writer = new PrintWriter("accounts.txt", "UTF-8");
            for(String l : lines){
                writer.println(l);
            }
            writer.close();
        }catch(Exception e){
            System.err.println("Corrupt file \"accounts\"");
            System.exit(1);
        }
    }
     
     
}