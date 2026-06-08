import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;
import jazzer.InputParser;

import org.axiondb.tools.Console;

public class Entrypoint {
    public static void recurseDirectories(File path) throws Exception {
        for(File inputFile: path.listFiles()) {
            if(inputFile.isFile()) {
                String input = InputParser.parseString(Files.readAllBytes(inputFile.toPath()));
               // Reads the contents of each file, parses it into a string, and passes it to the `entrypoint` method for execution.
                Entrypoint.entrypoint(input);
            } else {
                recurseDirectories(inputFile);
            }
        }
    }
    public static void main(String args[]) throws Exception {
        //Calls `recurseDirectories` with the path provided as a command-line argument
        recurseDirectories(new File(args[0]));
    }
}