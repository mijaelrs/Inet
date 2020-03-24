import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
 
 
public class TextHandler {
 
    private ArrayList<HashMap<String, String>> text;
    private String[] languages;
    private int lang = 1;
     
    public TextHandler(){
        instantiate();
    }
    
    /**
     * Gets the line in the list text that belongs to the given index. 
     */
    public String getLine(int index) {
    	return text.get(index).get(languages[lang]);
    }
    /**
     * It sets the language that belongs to the given index.
     */
    public void setLanguage(int index){
        lang = index;
    }
     
    /**
     * It returns and array with the size 5 with languages that are available.
     */
    public String[] getLanguages(){
        return languages;
    }
    
    /**
     * It reads in until it encounters a blank line in the text file and then saves the messages that is 
     * needed for the chosen language in the list "text". The languages is saved in the list "languages". 
     */
    private void instantiate() {
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("text.txt"), "UTF-8"));
            String line;
            text = new ArrayList<HashMap<String, String>>();
            ArrayList<String> tempLang = new ArrayList<String>();
            HashMap<String, String> map = new HashMap<String, String>();
            boolean foundAllLanguages = false;
            int counter = 0;
            while ((line = br.readLine()) != null) {
                if(line.matches("")){
                    if(!foundAllLanguages){
                        languages = new String[tempLang.size()];
                        for(int i = 0; i < tempLang.size(); i++){
                            languages[i] = tempLang.get(i);
                        }
                        foundAllLanguages = true;
                    }else{
                        counter = 0;
                        text.add(new HashMap<String, String>(map));
                        map.clear();
                    }
                }else{
                    if(!foundAllLanguages){
                        tempLang.add(line);
                    }else{
                        map.put(languages[counter], line);
                        counter++;
                    }
                }
            }
            br.close();
        }catch(Exception e){
            System.err.println("Corrupt file \"text\"");
            System.exit(1);
        }
    }
     
}