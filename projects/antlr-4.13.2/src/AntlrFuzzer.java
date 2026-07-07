import com.code_intelligence.jazzer.api.FuzzedDataProvider;

import java.nio.file.*;
import java.nio.charset.StandardCharsets;

public class AntlrFuzzer {
    public static void fuzzerTestOneInput(FuzzedDataProvider data) throws Exception {
        Path grammarDirectory = Files.createTempDirectory("grammar");
        Path grammarFile = grammarDirectory.resolve("grammar.g4");
        try {
            Files.write(grammarFile, data.consumeRemainingAsString().getBytes(StandardCharsets.UTF_8));
            Entrypoint.entrypoint(grammarDirectory);
        } finally {
            Files.deleteIfExists(grammarFile);
            Files.deleteIfExists(grammarDirectory);
        }
    }
}
