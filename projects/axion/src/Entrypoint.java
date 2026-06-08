import java.io.File;
import java.nio.file.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.StringTokenizer;
import java.util.stream.Stream;
import jazzer.InputParser;

import org.axiondb.tools.Console;

public class Entrypoint {
    public static void entrypoint(String input) throws Exception {
        //  - Creates a temporary directory and log file for the database.
        Path db = Files.createTempDirectory("fuzzing");
        Path logFile = db.resolve("log.txt");
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
            deleteDirectory(db.toFile());
        }
    }

    public static void recurseDirectories(Path path) throws Exception {
        try(Stream<Path> files = Files.walk(path)) {
            for(Path inputFile: (Iterable<Path>) files.filter(Files::isRegularFile)::iterator) {
                String input = InputParser.parseString(Files.readAllBytes(inputFile));
                // Reads the contents of each file, parses it into a string, and passes it to the `entrypoint` method for execution.
                Entrypoint.entrypoint(input);
            }
        }
    }

    public static boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }

    public static void main(String args[]) throws Exception {
        //Calls `recurseDirectories` with the path provided as a command-line argument
        recurseDirectories(Paths.get(args[0]));
    }
}