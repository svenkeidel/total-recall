import java.io.File;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.io.IOException;

import org.javacc.parser.*;

public class Entrypoint {

    public static void entrypoint(File grammarFile){
        try {
            System.out.println("showing path *********************************************************");
            System.out.println("Processing file: " + grammarFile.getAbsolutePath());
            System.out.println("*****************************************************");
            
            long fileSize = grammarFile.length();
            System.out.println("File size: " + fileSize + " lines");
    
            if (fileSize == 0) {
                System.out.println("File is empty, skipping processing.");
                return;
            }
    
            String content = Files.readString(grammarFile.toPath(), StandardCharsets.UTF_8);
            System.out.println("**************************************File content:\n" + content);
            System.out.println("**************************************End of file content:\n");

            
            // Call the parser main method
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
        if (args.length > 0) {
            recurseDirectories(new File(args[0]));
        } else {
            System.out.println("Please provide a directory path as an argument.");
        }
    }
}
