import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import java.io.StringReader;
import java.io.StringWriter;

import org.apache.batik.apps.rasterizer.SVGConverter;
import org.apache.batik.apps.rasterizer.DestinationType;

public class Entrypoint {
    public static void entrypoint(File svg) {
        try {
            SVGConverter converter = new SVGConverter();
            converter.setSources(new String[]{svg.getAbsolutePath()});
            File dst = Files.createTempDirectory("output").toFile();
            converter.setDst(dst);
            converter.execute();
            converter.setDestinationType(DestinationType.JPEG);
            converter.execute();
            converter.setDestinationType(DestinationType.TIFF);
            converter.execute();
            String[]files = dst.list();

            for(String f: files){
                Files.delete(dst.toPath().resolve(f));
            }
            Files.delete(dst.toPath());
        } catch (Throwable exc) {
            // Ignore exceptions
        }
    }
    public static void recurseDirectories(File path) throws IOException {
        for(File inputFile: path.listFiles()) {
            if(inputFile.isFile()) {
                Entrypoint.entrypoint(inputFile);
            } else {
                recurseDirectories(inputFile);
            }
        }
    }
    public static void main(String args[]) throws IOException {
        recurseDirectories(new File(args[0]));
    }
}