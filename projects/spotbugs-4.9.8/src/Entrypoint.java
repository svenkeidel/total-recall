import java.nio.file.*;
import java.io.IOException;
import java.util.stream.Stream;

import edu.umd.cs.findbugs.FindBugs2;

public class Entrypoint {
    public static void entrypoint(Path jar) {
        Path jdk26 = Paths.get("/resources/jdk-26-classes/");
        try {
            FindBugs2.main(new String[] {
                    "-effort:max",
                    "-experimental",
                    "-low", // report all bugs
                    "-relaxed", // Relaxed reporting mode. For many detectors, this option suppresses the heuristics used to avoid reporting false positives.
                    "-chooseVisitors", "+DontReusePublicIdentifiers,+CheckExpectedWarnings,+UselessSubclassMethod," +
                                       "+InefficientMemberAccess,+PublicSemaphores,+FindCircularDependencies," +
                                       "+BadAppletConstructor,+InefficientToArray,+InefficientIndexOf," +
                                       "+InefficientInitializationInsideLoop,+CovariantArrayAssignment," +
                                       "+CallToUnsupportedMethod,+EmptyZipFileEntry,+NoiseNullDeref,+ResolveAllReferences," +
                                       "+CheckCalls,+ViewCFG,+TestASM," +
                                       "+FindNonSerializableValuePassedToWriteObject,+FindNonSerializableStoreIntoSession",
                    "-sortByClass",
                    "-progress",
                    "-auxclasspath", jdk26.toString(),
                    jar.toString(),
            });
        } catch (Throwable exc) {
            // Ignore exceptions
            exc.printStackTrace(System.out);
        }
    }
    public static void recurseDirectories(Path path) throws Exception {
        try(Stream<Path> files = Files.walk(path)) {
            for(Path inputFile: (Iterable<Path>) files.filter(Files::isRegularFile)::iterator) {
                Entrypoint.entrypoint(inputFile);
            }
        }
    }
    public static void main(String args[]) throws Throwable {
        recurseDirectories(Paths.get(args[0]));
    }
}