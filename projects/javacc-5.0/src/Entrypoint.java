import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import org.javacc.parser.Main;
import org.javacc.jjdoc.JJDocMain;

public class Entrypoint {
  /**
     * 
     * @param grammarPath The grammar file to be processed.
     */
    public static void entrypoint(Path grammarPath) {
        try {
            Path output = Files.createTempDirectory("javacc");
            Path documentation = output.resolve("documentation.html");

            try {
                String grammar = grammarPath.toAbsolutePath().toString();
                org.javacc.parser.Main.mainProgram(new String[] { "-OUTPUT_DIRECTORY="+output.toAbsolutePath().toString(), grammar});
                org.javacc.jjdoc.JJDocMain.mainProgram(new String[]{ "-OUTPUT_FILE="+documentation.toAbsolutePath().toString(), grammar});
            } catch(Throwable t) {
                t.printStackTrace();
            } finally {
                deleteDirectory(output.toFile());
            }
        } catch(Throwable exc) {
            exc.printStackTrace();
        }

    }

    public static void recurseDirectories(Path path) throws Exception {
        try(Stream<Path> files = Files.walk(path)) {
            for(Path inputFile: (Iterable<Path>) files.filter(Files::isRegularFile)::iterator) {
                Entrypoint.entrypoint(inputFile);
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
        recurseDirectories(Paths.get(args[0]));
    }
}
