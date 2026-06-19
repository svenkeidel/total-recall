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

    private static Path db;

    public static void entrypoint(String input) throws Exception {
        Path logFile = db.resolve("log.txt");
        PrintWriter logWriter = new PrintWriter(logFile.toFile());
        Console console = null;
        try {
            console = new Console("db", db.toString(), logWriter);
            StringTokenizer tokenizer = new StringTokenizer(input, ";", false);
            while(tokenizer.hasMoreTokens()) {
                String sql = tokenizer.nextToken().replace("\n", "");
                try {
                    //Tokenizes the input string by splitting it on the `;` character to handle multiple SQL commands.
                    console.execute(sql);
                } catch (Throwable exc) {
                    exc.printStackTrace(System.err);
                }
            }
        } catch (Throwable exc) {
            exc.printStackTrace(System.err);
        } finally {
            console.cleanUp();
            deleteDirectoryContents(db.toFile(), true);
        }
    }

    public static void deleteDirectoryContents(File directoryToBeDeleted, Boolean root) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectoryContents(file, false);
            }
        }
        if(! root) {
            directoryToBeDeleted.delete();
        }
    }

    public static void main(String args[]) throws Exception {
        Entrypoint.db = Files.createTempDirectory("db");
        try(Stream<Path> files = Files.walk(Paths.get(args[0]))) {
            for(Path inputFile: (Iterable<Path>) files.filter(Files::isRegularFile)::iterator) {
                String input = InputParser.parseString(Files.readAllBytes(inputFile));
                Entrypoint.entrypoint(input);
            }
        }
    }
}