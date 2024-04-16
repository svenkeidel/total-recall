import com.code_intelligence.jazzer.api.FuzzedDataProvider;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;

public class FindbugsFuzzer {

    static {
        try {
            Entrypoint.patchBCELClasses();
        } catch (Throwable e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static void fuzzerTestOneInput(FuzzedDataProvider data) throws IOException {
        Path temp = Files.createTempFile("fuzzing", ".jar");
        try {
            Files.write(temp, data.consumeRemainingAsBytes());
            Entrypoint.entrypoint(temp.toFile());
        } finally {
            Files.delete(temp);
        }
    }
}
