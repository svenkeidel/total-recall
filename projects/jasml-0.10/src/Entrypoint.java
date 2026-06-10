import java.io.File;
import java.nio.file.*;
import java.util.stream.Stream;

import com.jasml.classes.JavaClass;
import com.jasml.compiler.JavaClassDumpper;
import com.jasml.compiler.SourceCodeParser;
import com.jasml.decompiler.JavaClassParser;
import com.jasml.decompiler.SourceCodeBuilder;

public class Entrypoint {
    public static void entrypoint(Path input) throws Exception {
        Path dumpedByteCode = Files.createTempFile("fuzzing", "dumpedByteCode");
        try {
            JavaClassParser parser = new JavaClassParser();
            JavaClass clazz = parser.parseClass(input.toFile());
            String textualJVMByteCode = new SourceCodeBuilder().toString(clazz);
            JavaClass parsedClazz = new SourceCodeParser(textualJVMByteCode).parse();
            new JavaClassDumpper(parsedClazz, dumpedByteCode.toFile()).dump();
        } catch (Throwable exc) {
            // Ignore exceptions
            exc.printStackTrace(System.err);
        } finally {
            Files.delete(dumpedByteCode);
        }
    }
    public static void recurseDirectories(Path path) throws Exception {
        try(Stream<Path> files = Files.walk(path)) {
            for(Path inputPath: (Iterable<Path>) files.filter(Files::isRegularFile)::iterator) {
                Entrypoint.entrypoint(inputPath);
            }
        }
    }
    public static void main(String args[]) throws Exception {
        recurseDirectories(Paths.get(args[0]));
    }
}