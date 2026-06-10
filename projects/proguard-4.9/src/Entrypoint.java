import java.io.File;
import java.nio.file.*;
import java.io.IOException;
import java.util.stream.Stream;
import java.util.ArrayList;

import proguard.ClassPath;
import proguard.ClassPathEntry;
import proguard.Configuration;
import proguard.ProGuard;

class Entrypoint {
    public static void entrypoint(Path inputJar) throws Exception {
        Path jdk = Paths.get("/resources/rt.jar");

        Path result = Files.createTempDirectory("result");
        Path outputJar = result.resolve("output.jar");

        try {
            Configuration configuration = new Configuration();

            configuration.programJars = new ClassPath();
            configuration.programJars.add(new ClassPathEntry(inputJar.toFile(), false));
            configuration.programJars.add(new ClassPathEntry(outputJar.toFile(), true));

            configuration.libraryJars = new ClassPath();
            configuration.libraryJars.add(new ClassPathEntry(jdk.toFile(), false));

            configuration.keepDirectories = new ArrayList();

            ProGuard proGuard = new ProGuard(configuration);
            proGuard.execute();
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