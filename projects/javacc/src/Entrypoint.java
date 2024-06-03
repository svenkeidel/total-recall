import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;

import org.javacc.parser.*;

public class Entrypoint {
    public static void entrypoint (File grammarFile){
        try {
            org.javacc.parser.Main.main(new String[] {grammarFile.getAbsolutePath()});
        } catch(Throwable t) {
            t.printStackTrace(); 
        }
    }
    
    
        public static void recurseDirectories(File path) throws IOException {
            for(File inputFile: path.listFiles()) {
                if(inputFile.isFile()) {
                    Entrypoint.entrypoint(inputFile);
                } else {
                    recurseDirectories(inputFile);
                }
            }
        }
        public static void main(String args[]) throws IOException {
            recurseDirectories(new File(args[0]));
        }
    
   
}
