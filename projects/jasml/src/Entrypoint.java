import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import com.jasml.classes.JavaClass;
import com.jasml.compiler.JavaClassDumpper;
import com.jasml.compiler.SourceCodeParser;
import com.jasml.decompiler.JavaClassParser;
import com.jasml.decompiler.SourceCodeBuilder;

public class Entrypoint {
    public static void entrypoint(File input) throws Exception {
        Path dumpedByteCode = Files.createTempFile("fuzzing", "dumpedByteCode");
        File dumpedByteCodeFile = dumpedByteCode.toFile();
        try {
            JavaClassParser parser = new JavaClassParser();
            JavaClass clazz = parser.parseClass(input);
            String textualJVMByteCode = new SourceCodeBuilder().toString(clazz);
            JavaClass parsedClazz = new SourceCodeParser(textualJVMByteCode).parse();
            new JavaClassDumpper(parsedClazz, dumpedByteCodeFile).dump();
        } catch (Throwable exc) {
            // Ignore exceptions
            exc.printStackTrace(System.err);
        } finally {
            dumpedByteCodeFile.deleteOnExit();
        }
    }
    public static void recurseDirectories(File path) throws Exception {
        for(File inputFile: path.listFiles()) {
            if(inputFile.isFile()) {
                Entrypoint.entrypoint(inputFile);
            } else {
                recurseDirectories(inputFile);
            }
        }
    }
    public static void main(String args[]) throws Exception {
        recurseDirectories(new File(args[0]));
    }
}