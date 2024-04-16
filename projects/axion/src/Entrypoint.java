import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;
import jazzer.InputParser;

import org.axiondb.tools.Console;

//import java.sql.Connection;
//import java.sql.Statement;
//import java.sql.PreparedStatement;
//import java.io.StringReader;
//import java.io.ByteArrayInputStream;
//import java.util.Random;
//import java.nio.charset.Charset;
//import java.sql.SQLException;
//import java.util.Arrays;

public class Entrypoint {
    public static void entrypoint(String input) throws Exception {
        Path db = Files.createTempDirectory("fuzzing");
        Path logFile = Files.createTempFile("fuzzing", "log");
        PrintWriter logWriter = new PrintWriter(logFile.toFile());
        Console console = null;
        try {
            console = new Console("fuzzingdb", db.toString(), logWriter);
//            createInitialTables(console.getConnection());
            StringTokenizer tokenizer = new StringTokenizer(input, ";", false);
            while(tokenizer.hasMoreTokens()) {
                String sql = tokenizer.nextToken().replace("\n", "");
                try {
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
            console.cleanUp();
            Runtime.getRuntime().exec("rm -rf "+db.toString()).waitFor();
        }
    }

//    private static void createInitialTables(Connection connection) {
//        try(Statement statement = connection.createStatement();
//            PreparedStatement insertClobs = connection.prepareStatement("INSERT INTO clobs VALUES (?)");
//            PreparedStatement insertCompressedClobs = connection.prepareStatement("INSERT INTO compressedclobs VALUES (?)");
//            PreparedStatement insertBlobs = connection.prepareStatement("INSERT INTO blobs VALUES (?)");
//            PreparedStatement insertCompressedBlobs = connection.prepareStatement("INSERT INTO compressedblobs VALUES (?)")) {
//
//            statement.execute("CREATE TABLE clobs (x CLOB)");
//            for (int i = 0; i < 100; i++) {
//                try {
//                    System.out.println(new org.axiondb.types.StringClob(randomString(100)) instanceof java.sql.Clob);
//                    System.out.println(new org.axiondb.types.LOBType().accepts(new org.axiondb.types.StringClob(randomString(100))));
//                    insertClobs.setClob(1, new org.axiondb.types.StringClob(randomString(100)));
//                    insertClobs.execute();
//                } catch (Exception exc) {
//                    exc.printStackTrace(System.err);
//                }
//            }
//
//            statement.execute("CREATE TABLE compressedclobs (x COMPRESSEDCLOB)");
//            for (int i = 0; i < 100; i++) {
//                try {
//                    insertCompressedClobs.setClob(1, new org.axiondb.types.StringClob(randomString(100)));
//                    insertCompressedClobs.execute();
//                } catch (Exception exc) {
//                    exc.printStackTrace(System.err);
//                }
//            }
//
//            statement.execute("CREATE TABLE blobs (x BLOB)");
//            for (int i = 0; i < 100; i++) {
//                try {
//                    insertBlobs.setBlob(1, new ByteArrayInputStream(randomString(100).getBytes()));
//                    insertBlobs.execute();
//                } catch (Exception exc) {
//                    exc.printStackTrace(System.err);
//                }
//            }
//
//            statement.execute("CREATE TABLE compressedblobs (x COMPRESSEDBLOB)");
//            for (int i = 0; i < 100; i++) {
//                try {
//                    insertCompressedBlobs.setBlob(1, new ByteArrayInputStream(randomString(100).getBytes()));
//                    insertCompressedBlobs.execute();
//                } catch (Exception exc) {
//                    exc.printStackTrace(System.err);
//                }
//            }
//        } catch (Exception exc) { exc.printStackTrace(System.err); }
//    }

//    private static Random random = new Random();
//
//    private static String randomString(int len) {
//        byte[] byteArray = new byte[100];
//        random.nextBytes(byteArray);
//        return new String(byteArray, Charset.forName("UTF-8"));
//    }
//
//    private static void deleteDirectory(File directoryToBeDeleted) {
//        File[] allContents = directoryToBeDeleted.listFiles();
//        if (allContents != null) {
//            for (File file : allContents) {
//                deleteDirectory(file);
//            }
//        }
//        System.err.println("to delete: "+Arrays.toString(directoryToBeDeleted.listFiles()));
//        System.err.println("delete "+directoryToBeDeleted.toString());
//        directoryToBeDeleted.delete();
//    }

    public static void recurseDirectories(File path) throws Exception {
        for(File inputFile: path.listFiles()) {
            if(inputFile.isFile()) {
                String input = InputParser.parseString(Files.readAllBytes(inputFile.toPath()));
                Entrypoint.entrypoint(input);
            } else {
                recurseDirectories(inputFile);
            }
        }
    }
    public static void main(String args[]) throws Exception {
        recurseDirectories(new File(args[0]));
    }
}