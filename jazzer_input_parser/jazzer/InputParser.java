package jazzer;

public class InputParser {
    static {
        System.loadLibrary("JazzerInputParser");
    }

    public static native String parseString(byte[] input);
}