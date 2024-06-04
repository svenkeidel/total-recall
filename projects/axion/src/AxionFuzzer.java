import com.code_intelligence.jazzer.api.FuzzedDataProvider;
//#  - Takes a `FuzzedDataProvider` object as input.
public class AxionFuzzer {
    public static void fuzzerTestOneInput(FuzzedDataProvider data) throws Exception {
      //  - Consumes the remaining data as a string and passes it to the `entrypoint` method.
      Entrypoint.entrypoint(data.consumeRemainingAsString());
    }
}
