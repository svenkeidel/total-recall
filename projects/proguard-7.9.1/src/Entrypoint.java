import java.io.File;
import java.nio.file.*;
import java.io.IOException;
import java.util.stream.Stream;
import java.util.ArrayList;

import proguard.*;

public class Entrypoint {

    private static Path result;
    public static void entrypoint(Path inputJar) {
        Path jdk = Paths.get("/resources/jmods/");

        Path outputJar = result.resolve("output.jar");

        try {
            Configuration configuration = new Configuration();
            ConfigurationParser parser = new ConfigurationParser(new String[]{
                    "-injars", inputJar.toString(),
                    "-outjars", outputJar.toString(),
                    "-libraryjars", jdk.toString() + "(!**.jar;!module-info.class)",
                    "-optimizationpasses", "3",
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
                System.getProperties()
            );

            try {
                parser.parse(configuration);
            } finally {
                parser.close();
            }

            new ProGuard(configuration).execute();
        } catch (Throwable exc) {
            exc.printStackTrace(System.err);
        } finally {
            deleteDirectoryContents(result.toFile(), true);
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
        Entrypoint.result = Files.createTempDirectory("result");
        try(Stream<Path> files = Files.walk(Paths.get(args[0]))) {
            for(Path inputFile: (Iterable<Path>) files.filter(Files::isRegularFile)::iterator) {
                Entrypoint.entrypoint(inputFile);
            }
        }
    }

}