import com.code_intelligence.jazzer.api.FuzzedDataProvider;

public class AxionFuzzer {
    public static void fuzzerTestOneInput(FuzzedDataProvider data) throws Exception {
      Entrypoint.entrypoint(data.consumeRemainingAsString());
    }
}
