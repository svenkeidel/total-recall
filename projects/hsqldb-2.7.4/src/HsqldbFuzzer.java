import com.code_intelligence.jazzer.api.FuzzedDataProvider;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HsqldbFuzzer {
    public static void fuzzerTestOneInput(FuzzedDataProvider data) throws Exception {
      //  - Consumes the remaining data as a string and passes it to the `entrypoint` method.
        Path sqlFile = Files.createTempFile("fuzzing", ".hsqldb");
        try {
            Files.write(sqlFile, data.consumeRemainingAsString().getBytes());
            Entrypoint.entrypoint(sqlFile);
        } finally {
            Files.delete(sqlFile);
        }
    }
}
