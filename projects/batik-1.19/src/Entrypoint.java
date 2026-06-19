import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.stream.Stream;

import org.apache.batik.apps.rasterizer.SVGConverter;
import org.apache.batik.apps.rasterizer.DestinationType;

public class Entrypoint {

    private static File dst;
    public static void entrypoint(Path svg) {
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
            deleteDirectoryContents(dst, true);
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
        Entrypoint.dst = Files.createTempDirectory("dst").toFile();
        try(Stream<Path> files = Files.walk(Paths.get(args[0]))) {
            for(Path inputFile: (Iterable<Path>) files.filter(Files::isRegularFile)::iterator) {
                Entrypoint.entrypoint(inputFile);
            }
        }
    }
}