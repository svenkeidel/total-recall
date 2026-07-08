import java.io.File;
import java.nio.file.*;
import java.io.IOException;
import java.util.stream.Stream;
import java.util.ArrayList;
import java.util.Set;

import proguard.*;

public class Entrypoint {

    private static Path result;
    private static final String optimizations =
            "library/gson,class/marking/final," +
            "class/unboxing/enum," +
            "class/merging/vertical," +
            "class/merging/horizontal," +
            "class/merging/wrapper," +
            "field/removal/writeonly," +
            "field/marking/private," +
            "field/generalization/class," +
            "field/specialization/type," +
            "field/propagation/value," +
            "method/marking/private," +
            "method/marking/static," +
            "method/marking/final," +
            "method/marking/synchronized," +
            "method/removal/parameter," +
            "method/generalization/class," +
            "method/specialization/parametertype," +
            "method/specialization/returntype," +
            "method/propagation/parameter," +
            "method/propagation/returnvalue," +
            "method/inlining/short,method/inlining/unique," +
            "method/inlining/tailrecursion," +
            "code/merging," +
            "code/simplification/variable," +
            "code/simplification/arithmetic," +
            "code/simplification/cast," +
            "code/simplification/field," +
            "code/simplification/branch," +
            "code/simplification/object," +
            "code/simplification/string," +
            "code/simplification/math," +
            "code/simplification/advanced," +
            "code/removal/advanced," +
            "code/removal/simple," +
            "code/removal/variable," +
            "code/removal/exception," +
            "code/allocation/variable";

    public static void entrypoint(Path inputJar) {
        Path jdk = Paths.get("/resources/jmods/");

        Path outputJar = result.resolve("output.jar");
        Path dump = result.resolve("dump");
        Path configFile = result.resolve("configuration.pro");
        Path mapping = result.resolve("mapping.txt");

        Set<String> backport = Set.of("axion-1.0-M2.jar", "batik-1.7.jar", "findbugs-1.3.9.jar", "hsqldb-2.0.0.jar", "jasml-0.10.jar", "javacc-5.0.jar", "proguard-4.9.jar", "xerces-2.10.0.jar");

        try {
            Configuration configuration = new Configuration();
            ConfigurationParser parser = new ConfigurationParser(new String[]{
                    "-injars", inputJar.toString(),
                    "-outjars", outputJar.toString(),
                    "-libraryjars", jdk.toString() + "(!**.jar;!module-info.class)",
                    "-verbose",
                    backport.contains(inputJar.getFileName().toString()) ? "-target" : "", backport.contains(inputJar.getFileName().toString()) ? "1.6" : "",
                    "-dump", dump.toString(),
                    "-printseeds",
                    "-printconfiguration", configFile.toString(),
                    "-printmapping", mapping.toString(),
                    "-optimizations", optimizations,
                    "-optimizationpasses", "2",
                    "-optimizeaggressively",
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

                    "-assumenoexternalsideeffects class java.lang.StringBuilder {" +
                    "    public java.lang.StringBuilder();" +
                    "    public java.lang.StringBuilder(int);" +
                    "    public java.lang.StringBuilder(java.lang.String);" +
                    "    public java.lang.StringBuilder append(java.lang.Object);" +
                    "    public java.lang.StringBuilder append(java.lang.String);" +
                    "    public java.lang.StringBuilder append(java.lang.StringBuffer);" +
                    "    public java.lang.StringBuilder append(char[]);" +
                    "    public java.lang.StringBuilder append(char[], int, int);" +
                    "    public java.lang.StringBuilder append(boolean);" +
                    "    public java.lang.StringBuilder append(char);" +
                    "    public java.lang.StringBuilder append(int);" +
                    "    public java.lang.StringBuilder append(long);" +
                    "    public java.lang.StringBuilder append(float);" +
                    "    public java.lang.StringBuilder append(double);" +
                    "    public java.lang.String toString();" +
                    "}",
                    "-assumenoexternalreturnvalues class java.lang.StringBuilder {" +
                    "    public java.lang.StringBuilder append(java.lang.Object);" +
                    "    public java.lang.StringBuilder append(java.lang.String);" +
                    "    public java.lang.StringBuilder append(java.lang.StringBuffer);" +
                    "    public java.lang.StringBuilder append(char[]);" +
                    "    public java.lang.StringBuilder append(char[], int, int);" +
                    "    public java.lang.StringBuilder append(boolean);" +
                    "    public java.lang.StringBuilder append(char);" +
                    "    public java.lang.StringBuilder append(int);" +
                    "    public java.lang.StringBuilder append(long);" +
                    "    public java.lang.StringBuilder append(float);" +
                    "    public java.lang.StringBuilder append(double);" +
                    "}",
                    "-keepkotlinmetadata",
                    "-dontskipnonpubliclibraryclasses",
                    "-dontskipnonpubliclibraryclassmembers",
                    "-dontwarn"
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