import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.*;
import java.util.stream.Stream;

import java.util.HashMap;
import java.util.Map;

import java.sql.Connection;
import java.sql.DriverManager;

import org.hsqldb.cmdline.SqlFile;

public class Entrypoint {
    public static void entrypoint(Path input) throws Exception {
        // Create a temporary directory for the database.
        Path db = Files.createTempDirectory("fuzzing");

        try {
            Connection conn = DriverManager.getConnection("jdbc:hsqldb:file:" + db.toString() + "/testdb", "SA", "");

            SqlFile sqlFile = new SqlFile(input.toFile());
            Map<String, String> userVars = new HashMap<>();
            userVars.put("*SCRIPT_DIR", input.getParent().toString());
            sqlFile.addUserVars(userVars);
            sqlFile.setConnection(conn);
            sqlFile.execute();
        } catch (Throwable exc) {
            exc.printStackTrace(System.err);
        } finally {
            // Clean up the temporary files and directory
            deleteDirectory(db.toFile());
        }
    }


    public static void recurseDirectories(Path path) throws Exception {
        try(Stream<Path> files = Files.walk(path)) {
            for(Path inputPath: (Iterable<Path>) files.filter(Files::isRegularFile)::iterator) {
                Entrypoint.entrypoint(inputPath);
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

    public static void main(String[] args) throws Exception {
        // Calls `recurseDirectories` with the path provided as a command-line argument
        recurseDirectories(Paths.get(args[0]));
    }
}
