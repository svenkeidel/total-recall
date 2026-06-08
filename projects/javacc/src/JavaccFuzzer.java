import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;

public class JavaccFuzzer {

    public static void fuzzerTestOneInput(FuzzedDataProvider data) throws Exception {
        // Create a temporary file for fuzzing input
        Path temp = Files.createTempFile("fuzzing", ".jj");
        try {
            Files.write(temp, data.consumeRemainingAsString().getBytes());
            Entrypoint.entrypoint(temp);
        } finally {
            Files.delete(temp);
        }
    }
}
