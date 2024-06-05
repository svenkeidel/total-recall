import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;

public class JavaccFuzzer {

    public static void fuzzerTestOneInput(FuzzedDataProvider data) throws Exception {
        // Create a temporary file for fuzzing input
        Path temp = Files.createTempFile("fuzzing", ".jj");
        try {
            byte[] fuzzData = data.consumeRemainingAsBytes();
            Files.write(temp, fuzzData);

            // Log the generated fuzzing data
            String fuzzContent = new String(fuzzData, StandardCharsets.UTF_8);
            System.out.println("Generated fuzzing file content:\n" + fuzzContent);

            // Pass the file to the entrypoint method
            Entrypoint.entrypoint(temp.toFile());
        } finally {
            Files.delete(temp);
        }
    }
}
