import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class JavaccFuzzer {

    public static void fuzzerTestOneInput(FuzzedDataProvider data) throws Exception {
        //  - Consumes the remaining data as a string and passes it to the `entrypoint` method.
        Path temp = Files.createTempFile("fuzzing", ".jj");
        try {
            Files.write(temp, data.consumeRemainingAsBytes());
            Entrypoint.entrypoint(temp.toFile());
        } finally {
            Files.delete(temp);
        }     
     }
}