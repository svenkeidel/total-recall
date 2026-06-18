import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.stream.Stream;

import org.apache.lucene.demo.IndexFiles;
import org.apache.lucene.demo.SearchFiles;

public class Entrypoint {
    public static void entrypoint(Path docs) {
        try {
            File index = Files.createTempDirectory("index").toFile();
            try {
                System.out.println(docs.toString());
                IndexFiles.main(new String[]{
                        "-index", index.toString(),
                        "-docs", docs.toString(),
                        "-knn_dict", "/resources/knn_dict/glove-6B-50d-sorted.txt"
                });
                SearchFiles.main(new String[]{
                        "-index", index.toString(),
                        "-query", "the",
                        "-paging", String.valueOf(Integer.MAX_VALUE),
                        "-knn_vector", "3"
                });
            } finally {
//                deleteDirectory(index);
            }
        } catch (Throwable exc) {
            exc.printStackTrace(System.err);
        }
    }


    public static boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }

    public static void main(String args[]) throws Exception {
        try(Stream<Path> fileList = Files.list(Paths.get(args[0]))) {
            for(Path docsPath: (Iterable<Path>) fileList::iterator) {
                Entrypoint.entrypoint(docsPath);
            }
        }
    }
}