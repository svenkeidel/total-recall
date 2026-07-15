import com.code_intelligence.jazzer.api.FuzzedDataProvider;
public class Log4JFuzzer {
    public static void fuzzerTestOneInput(FuzzedDataProvider data) throws Exception {
        Entrypoint.entrypoint();
    }
}
