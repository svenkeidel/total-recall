import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

import java.sql.Connection;

import org.hsqldb.cmdline.SqlFile;
import org.hsqldb.cmdline.SqlFile;
import org.hsqldb.jdbc.JDBCDriver;
import org.hsqldb.persist.HsqlProperties;

public class Entrypoint {

    private static Path db;
    private static long processed = 0;
    private static long total = 0;
    public static void entrypoint(Path input) {
        System.out.println(String.format("[%d/%d]: %s", processed, total, input.toString()));

        try {
            Properties properties = new Properties();
            properties.put("user", "SA");
            properties.put("password", "");

            List<String> connectionStrings = new ArrayList<>();
            connectionStrings.add("jdbc:hsqldb:file:" + db.toString() + "/testdb"+processed);
            connectionStrings.add("jdbc:hsqldb:mem:testdb"+processed);

            JDBCDriver driver = new JDBCDriver();

            for(String connectionString: connectionStrings) {
                try(Connection conn = driver.connect(connectionString, properties)) {
                    SqlFile sqlFile = new SqlFile(input.toFile());
                    Map<String, String> userVars = new HashMap<>();
                    userVars.put("*SCRIPT_DIR", input.getParent().toString());
                    sqlFile.addUserVars(userVars);
                    sqlFile.setConnection(conn);
                    sqlFile.execute();
                } catch (Throwable exc) {
                    exc.printStackTrace(System.err);
                }
            }
        } finally {
            processed += 1;
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
        try (Stream<Path> stream = Files.walk(Paths.get(args[0]))) { Entrypoint.total = stream.filter(Files::isRegularFile).count(); }
        try(Stream<Path> files = Files.walk(Paths.get(args[0]))) {
            for(Path inputFile: (Iterable<Path>) files.filter(Files::isRegularFile)::iterator) {
                Entrypoint.entrypoint(inputFile);
            }
        }
    }
}
