import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import org.javacc.parser.Main;
import org.javacc.jjdoc.JJDocMain;

public class Entrypoint {
  /**
     * 
     * @param grammarFile The grammar file to be processed.
     */
    public static void entrypoint(File grammarFile) throws IOException {

        Path output = Files.createTempDirectory("javacc");
        Path documentation = output.resolve("documentation.html");

        try {
            org.javacc.parser.Main.main(new String[] { "-OUTPUT_DIRECTORY="+output.toAbsolutePath().toString(), grammarFile.getAbsolutePath()});
            org.javacc.jjdoc.JJDocMain.main(new String[]{ "-OUTPUT_FILE="+documentation.toAbsolutePath().toString(), grammarFile.getAbsolutePath()});
        } catch(Throwable t) {
            t.printStackTrace(); 
        } finally {
            deleteDirectory(output.toFile());
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
        recurseDirectories(new File(args[0]));
    }
}
