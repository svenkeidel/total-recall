import com.code_intelligence.jazzer.api.FuzzedDataProvider;

import java.nio.file.Files;
import java.nio.file.Path;

public class JasmlFuzzer {
    public static void fuzzerTestOneInput(FuzzedDataProvider data) throws Exception {
        Path temp = Files.createTempFile("fuzzing", ".class");
        try {
            Files.write(temp, data.consumeRemainingAsBytes());
            Entrypoint.entrypoint(temp.toFile());
        } finally {
            Files.delete(temp);
        }

    }
}
