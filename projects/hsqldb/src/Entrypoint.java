import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.Map;

import java.sql.Connection;
import java.sql.DriverManager;

import org.hsqldb.cmdline.SqlFile;

public class Entrypoint {
    public static void entrypoint(File input) throws Exception {
        // Create a temporary directory for the database.
        Path db = Files.createTempDirectory("fuzzing");

        try {
            Connection conn = DriverManager.getConnection("jdbc:hsqldb:file:" + db.toString() + "/testdb", "SA", "");

            SqlFile sqlFile = new SqlFile(input);
            sqlFile.addUserVars(Map.of(
                "*SCRIPT_DIR", input.getParent()
            ));
            sqlFile.setConnection(conn);
            sqlFile.execute();
        } catch (Throwable exc) {
            exc.printStackTrace(System.err);
        } finally {
            // Clean up the temporary files and directory
            Runtime.getRuntime().exec("rm -rf " + db.toString()).waitFor();
        }
    }

    // Recursively processes files in a given directory.
    public static void recurseDirectories(File path) throws Exception {
        if (path.isFile()) {
            // Reads the contents of each file, parses it into a string, and passes it to the `entrypoint` method for execution.
            Entrypoint.entrypoint(path);
        } else {
            for (File file : path.listFiles()) {
                recurseDirectories(file);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        // Calls `recurseDirectories` with the path provided as a command-line argument
        recurseDirectories(new File(args[0]));
    }
}
