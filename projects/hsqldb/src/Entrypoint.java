import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;
import org.hsqldb.cmdline.SqlTool;

public class Entrypoint {
    public static void entrypoint(String input) throws Exception {
        // Create a temporary directory for the database.
        Path db = Files.createTempDirectory("fuzzing");

        // Create a temporary file to hold the SQL input
        Path sqlFile = Files.createTempFile("fuzzing", ".hsqldb");
        Files.write(sqlFile, input.getBytes());

        // Create the args array for SqlTool
        String[] args = {
            "--inlineRc=url=jdbc:hsqldb:file:" + db.toString() + "/testdb,user=ANAS,password=",
            sqlFile.toString()
        };

        try {
            SqlTool.objectMain(args);
        } catch (Throwable exc) {
            exc.printStackTrace(System.err);
        } finally {
            // Clean up the temporary files and directory
            Runtime.getRuntime().exec("rm -rf " + db.toString()).waitFor();
        }
    }

    // Recursively processes files in a given directory.
    public static void recurseDirectories(File path) throws Exception {
        for (File inputFile : path.listFiles()) {
            if (inputFile.isFile()) {
                String input = new String(Files.readAllBytes(inputFile.toPath()));
                // Reads the contents of each file, parses it into a string, and passes it to the `entrypoint` method for execution.
                HSQLDBEntrypoint.entrypoint(input);
            } else {
                recurseDirectories(inputFile);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        // Calls `recurseDirectories` with the path provided as a command-line argument
        recurseDirectories(new File(args[0]));
    }
}
