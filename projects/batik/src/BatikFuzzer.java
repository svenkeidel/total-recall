import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import com.code_intelligence.jazzer.api.BugDetectors;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;

public class BatikFuzzer {
    public static void fuzzerTestOneInput(FuzzedDataProvider data) throws IOException {
        BugDetectors.allowNetworkConnections();
        Path temp = Files.createTempFile("fuzzing", ".svg");
        try {
            Files.write(temp, data.consumeRemainingAsBytes());
            Entrypoint.entrypoint(temp.toFile());
        } finally {
            Files.delete(temp);
        }
    }
}
