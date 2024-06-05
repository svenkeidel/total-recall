import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;
import jazzer.InputParser;

import org.axiondb.tools.Console;

public class Entrypoint {
    public static void entrypoint(String input) throws Exception {
        //  - Creates a temporary directory and log file for the database.
        Path db = Files.createTempDirectory("fuzzing");

        Path logFile = Files.createTempFile("fuzzing", "log");
        PrintWriter logWriter = new PrintWriter(logFile.toFile());
        Console console = null;
        try {
            console = new Console("fuzzingdb", db.toString(), logWriter);
//            createInitialTables(console.getConnection());
//Tokenizes the input string by splitting it on the `;` character to handle multiple SQL commands.
            StringTokenizer tokenizer = new StringTokenizer(input, ";", false);
            while(tokenizer.hasMoreTokens()) {
                String sql = tokenizer.nextToken().replace("\n", "");
                try {
                    //Tokenizes the input string by splitting it on the `;` character to handle multiple SQL commands.
                    console.execute(sql);
                } catch (Throwable exc) {
                    // Ignore exceptions
                    exc.printStackTrace(System.err);
                }
            }
        } catch (Throwable exc) {
            // Ignore exceptions
            exc.printStackTrace(System.err);
        } finally {
            //Tokenizes the input string by splitting it on the `;` character to handle multiple SQL commands.
            console.cleanUp();
            Runtime.getRuntime().exec("rm -rf "+db.toString()).waitFor();
        }
    }




//  - Recursively processes files in a given directory.
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