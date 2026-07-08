import java.io.File;
import java.nio.file.*;
import java.io.IOException;
import java.util.stream.Stream;
import java.util.ArrayList;

import proguard.*;

class Entrypoint {

    static Path result;

    private static final String optimizations =
            "class/marking/final," +
            "class/merging/vertical," +
            "class/merging/horizontal," +
            "field/removal/writeonly," +
            "field/marking/private," +
            "field/propagation/value," +
            "method/marking/private," +
            "method/marking/static," +
            "method/marking/final," +
            "method/removal/parameter," +
            "method/propagation/parameter," +
            "method/propagation/returnvalue," +
            "method/inlining/short," +
            "method/inlining/unique," +
            "method/inlining/tailrecursion," +
            "code/merging," +
            "code/simplification/variable," +
            "code/simplification/arithmetic," +
            "code/simplification/cast," +
            "code/simplification/field," +
            "code/simplification/branch," +
            "code/simplification/string," +
            "code/simplification/advanced," +
            "code/removal/advanced," +
            "code/removal/simple," +
            "code/removal/variable," +
            "code/removal/exception," +
            "code/allocation/variable";


    public static void entrypoint(Path inputJar) {
        Path jdk = Paths.get("/resources/rt.jar");
        Path outputJar = result.resolve("output.jar");
        Path dump = result.resolve("dump");
        Path configFile = result.resolve("configuration.pro");
        Path mapping = result.resolve("mapping.txt");

        try {
            Configuration configuration = new Configuration();
            ConfigurationParser parser = new ConfigurationParser(new String[]{
                    "-injars", inputJar.toString(),
                    "-outjars", outputJar.toString(),
                    "-libraryjars", jdk.toString() + "(!**.jar;!module-info.class)",
                    "-verbose",
                    "-target", "1.4",
                    "-dump", dump.toString(),
                    "-printseeds",
                    "-printconfiguration", configFile.toString(),
                    "-printmapping", mapping.toString(),
                    "-optimizations", optimizations,
                    "-optimizationpasses", "2",
                    "-overloadaggressively",
                    "-allowaccessmodification",
                    "-mergeinterfacesaggressively",

                    "-keep", "class Entrypoint",

                    "-keep", "class com.google.gson.** { *; }",
                    "-keep", "class org.axiondb.** { *; }",
                    "-keep", "class org.apache.batik.** { *; }",
                    "-keep", "class org.hsqldb.** { *; }",
                    "-keep", "class com.jasml.** { *; }",
                    "-keep", "class com.javacc.** { *; }",
                    "-keep", "class proguard.** { *; }",
                    "-keep", "class edu.umd.cs.findbugs.** { *; }",
                    "-keep", "class com.jakewharton.gradle.dependencies.** { *; }",
                    "-keep", "class org.apache.xerces.** { *; }",

                    "-keep", "class kotlin.jvm.functions.** { *; }",
                    "-keep", "class kotlin.Metadata { *; }",
                    "-keepattributes", "*Annotation*,Signature,InnerClasses,EnclosingMethod",
                    "-keepattributes", "kotlin.Metadata,kotlin.annotations.**",

                    "-dontskipnonpubliclibraryclasses",
                    "-dontskipnonpubliclibraryclassmembers",
                    "-dontwarn"
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