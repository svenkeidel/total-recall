import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.stream.Stream;

import org.apache.batik.apps.rasterizer.SVGConverter;
import org.apache.batik.apps.rasterizer.DestinationType;

public class Entrypoint {
    public static void entrypoint(Path svg) {
        try {
            File dst = Files.createTempDirectory("output").toFile();
            try {
                SVGConverter converter = new SVGConverter();
                converter.setSources(new String[]{svg.toAbsolutePath().toString()});
                converter.setDst(dst);

                try {
                    converter.execute();
                } catch(Exception exc) {
                    exc.printStackTrace(System.err);
                }

                try {
                    converter.setDestinationType(DestinationType.JPEG);
                    converter.execute();
                } catch(Exception exc) {
                    exc.printStackTrace(System.err);
                }

                try {
                    converter.setDestinationType(DestinationType.TIFF);
                    converter.execute();
                } catch(Exception exc) {
                    exc.printStackTrace(System.err);
                }

                try {
                    converter.setDestinationType(DestinationType.PDF);
                    converter.execute();
                } catch(Exception exc) {
                    exc.printStackTrace(System.err);
                }
            } catch (Throwable exc) {
                exc.printStackTrace(System.err);
            } finally {
                deleteDirectory(dst);
            }
        } catch (Throwable exc) {
            exc.printStackTrace(System.err);
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

    public static void recurseDirectories(Path path) throws Exception {
        try(Stream<Path> files = Files.walk(path)) {
            for(Path inputFile: (Iterable<Path>) files.filter(Files::isRegularFile)::iterator) {
                Entrypoint.entrypoint(inputFile);
            }
        }
    }

    public static void main(String args[]) throws Exception {
        recurseDirectories(Paths.get(args[0]));
    }
}