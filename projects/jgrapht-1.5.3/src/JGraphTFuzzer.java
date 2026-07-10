import com.code_intelligence.jazzer.api.FuzzedDataProvider;
public class JGraphTFuzzer {
    public static void fuzzerTestOneInput(FuzzedDataProvider data) throws Exception {
        Entrypoint.entrypoint();
    }
}
