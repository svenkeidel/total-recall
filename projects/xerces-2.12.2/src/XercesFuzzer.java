import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import com.code_intelligence.jazzer.api.BugDetectors;

public class XercesFuzzer {
    public static void fuzzerTestOneInput(FuzzedDataProvider data) {
        BugDetectors.allowNetworkConnections();
        Entrypoint.entrypoint(data.consumeRemainingAsString());
    }
}
