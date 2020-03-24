import java.util.Random;
 
 
public class GenerateAccounts {
 
    public static void main(String[] args) {
        Random random = new Random();
        for(int accounts = 0; accounts < 10; accounts++){
            StringBuilder sb = new StringBuilder();
            for(int digits = 0; digits < 16; digits++){
                sb.append(random.nextInt(10));
            }
            sb.append(" ");
            for(int pin = 0; pin < 4; pin++){
                sb.append(random.nextInt(10));
            }
            System.out.println(sb.toString());
        }
    }
 
}