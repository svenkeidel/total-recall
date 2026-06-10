import com.code_intelligence.jazzer.api.FuzzedDataProvider;

import java.nio.file.Files;
import java.nio.file.Path;

class ProguardFuzzer {
    public static void fuzzerTestOneInput(FuzzedDataProvider data) throws Exception {
        Path temp = Files.createTempFile("fuzzing", ".jar");
        try {
            Files.write(temp, data.consumeRemainingAsBytes());
            Entrypoint.entrypoint(temp);
        } finally {
            Files.delete(temp);
        }
    }
}