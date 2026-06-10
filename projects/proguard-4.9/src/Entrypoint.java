import java.io.File;
import java.nio.file.*;
import java.io.IOException;
import java.util.stream.Stream;
import java.util.ArrayList;

import proguard.*;

class Entrypoint {
    public static void entrypoint(Path inputJar) throws Exception {
        Path jdk = Paths.get("/resources/rt.jar");

        Path result = Files.createTempDirectory("result");
        Path outputJar = result.resolve("output.jar");

        try {
            Configuration configuration = new Configuration();
            ConfigurationParser parser = new ConfigurationParser(new String[] {
                    "-injars", inputJar.toString(),
                    "-outjars", outputJar.toString(),
                    "-libraryjars", jdk.toString()+"(!**.jar;!module-info.class)",
                    "-optimizationpasses", "5",
                    "-overloadaggressively",
                    "-allowaccessmodification",
                    "-mergeinterfacesaggressively",
                    "-useuniqueclassmembernames",
                    "-repackageclasses", "''",
                    "-keep", "public", "class", "Entrypoint",
                    "-keepattributes", "SourceFile,LineNumberTable,Signature,InnerClasses,EnclosingMethod,Deprecated,Annotation",
                    "-dontskipnonpubliclibraryclasses",
                    "-dontskipnonpubliclibraryclassmembers"
                },
                System.getProperties());
            try {
                parser.parse(configuration);
            } finally {
                parser.close();
            }

            new ProGuard(configuration).execute();
        } catch (Throwable exc) {
            exc.printStackTrace(System.err);
        } finally {
            deleteDirectory(result.toFile());
        }
    }

    public static void recurseDirectories(Path path) throws Exception {
        try(Stream<Path> files = Files.walk(path)) {
            for(Path inputFile: (Iterable<Path>) files.filter(Files::isRegularFile)::iterator) {
                // Reads the contents of each file, parses it into a string, and passes it to the `entrypoint` method for execution.
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

    public static void main(String[] args) throws Exception {
        recurseDirectories(Paths.get(args[0]));
    }
}