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
    private static Path db;
    public static void entrypoint(Path input) {
        try(Connection conn = DriverManager.getConnection("jdbc:hsqldb:file:" + db.toString() + "/testdb", "SA", "")) {
            SqlFile sqlFile = new SqlFile(input.toFile());
            Map<String, String> userVars = new HashMap<>();
            userVars.put("*SCRIPT_DIR", input.getParent().toString());
            sqlFile.addUserVars(userVars);
            sqlFile.setConnection(conn);
            sqlFile.execute();
        } catch (Throwable exc) {
            exc.printStackTrace(System.err);
        } finally {
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
    public static void main(String[] args) throws Exception {
        Entrypoint.db = Files.createTempDirectory("db");
        try(Stream<Path> files = Files.walk(Paths.get(args[0]))) {
            for(Path inputFile: (Iterable<Path>) files.filter(Files::isRegularFile)::iterator) {
                Entrypoint.entrypoint(inputFile);
            }
        }
    }
}
