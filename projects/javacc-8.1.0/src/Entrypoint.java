import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import org.javacc.parser.Main;
import org.javacc.jjdoc.JJDocMain;

public class Entrypoint {
    private static Path output;

    public static void entrypoint(Path grammarPath) {

        Path documentation = output.resolve("documentation.html");

        try {
            String grammar = grammarPath.toAbsolutePath().toString();
            org.javacc.parser.Main.mainProgram(new String[] { "-OUTPUT_DIRECTORY="+output.toAbsolutePath().toString(), grammar});
            org.javacc.jjdoc.JJDocMain.mainProgram(new String[]{ "-OUTPUT_FILE="+documentation.toAbsolutePath().toString(), grammar});
        } catch(Throwable t) {
            t.printStackTrace();
        } finally {
            deleteDirectoryContents(output.toFile(), true);
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
        Entrypoint.output = Files.createTempDirectory("javacc");
        try(Stream<Path> files = Files.walk(Paths.get(args[0]))) {
            for(Path inputFile: (Iterable<Path>) files.filter(Files::isRegularFile)::iterator) {
                Entrypoint.entrypoint(inputFile);
            }
        }
    }
}
