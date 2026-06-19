import java.io.File;
import java.nio.file.*;
import java.util.stream.Stream;

import com.jasml.classes.JavaClass;
import com.jasml.compiler.JavaClassDumpper;
import com.jasml.compiler.SourceCodeParser;
import com.jasml.decompiler.JavaClassParser;
import com.jasml.decompiler.SourceCodeBuilder;

public class Entrypoint {

    private static Path dumpedByteCode;

    public static void entrypoint(Path input) {
        try {
            JavaClassParser parser = new JavaClassParser();
            JavaClass clazz = parser.parseClass(input.toFile());
            String textualJVMByteCode = new SourceCodeBuilder().toString(clazz);
            JavaClass parsedClazz = new SourceCodeParser(textualJVMByteCode).parse();
            new JavaClassDumpper(parsedClazz, dumpedByteCode.toFile()).dump();
        } catch (Throwable exc) {
            exc.printStackTrace(System.err);
        } finally {
            try {
                Files.deleteIfExists(dumpedByteCode);
            } catch (Throwable exc) {
                exc.printStackTrace(System.err);
            }
        }
    }

    public static void main(String args[]) throws Exception {
        Entrypoint.dumpedByteCode = Files.createTempFile("output", "bytecode");

        try(Stream<Path> files = Files.walk(Paths.get(args[0]))) {
            for(Path inputPath: (Iterable<Path>) files.filter(Files::isRegularFile)::iterator) {
                Entrypoint.entrypoint(inputPath);
            }
        }
    }
}