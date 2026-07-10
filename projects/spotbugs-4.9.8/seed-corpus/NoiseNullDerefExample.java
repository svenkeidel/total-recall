import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class NoiseNullDerefExample {

    public void processFile(String filePath) {
        BufferedReader reader = null;
        try {
            // If the constructor fails and throws an IOException,
            // 'reader' remains explicitly null.
            reader = new BufferedReader(new FileReader(filePath));

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            // FindBugs/SpotBugs triggers NoiseNullDeref / NP_GUARANTEED_DEREF_ON_EXCEPTION_PATH here.
            // If the try block failed at initialization, reader.close() guarantees a NullPointerException.
            try {
                reader.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}