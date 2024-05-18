import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import org.parser.Main
public class Entrypoint {


    public static void recurseDirectories(File path) throws IOException {
        for(File inputFile: path.listFiles()) {
            if(inputFile.isFile()) {
                String input = InputParser.parseString(Files.readAllBytes(inputFile.toPath()));
                Entrypoint.entrypoint(input);
            } else {
                recurseDirectories(inputFile);
            }
        }
    }
    public static void main(String args[]) throws IOException {
        recurseDirectories(new File(args[0]));
    }
   
}