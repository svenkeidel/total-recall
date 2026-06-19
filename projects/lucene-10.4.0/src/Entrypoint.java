import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.stream.Stream;

import org.apache.lucene.demo.IndexFiles;
import org.apache.lucene.demo.SearchFiles;

public class Entrypoint {

    private static File index;
    public static void entrypoint(Path docs) {
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
            deleteDirectoryContents(index, true);
        }
    }

    public static void deleteDirectoryContents(File directoryToBeDeleted, Boolean root) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectoryContents(file, false);
            }
        }
        if(! root) {
            directoryToBeDeleted.delete();
        }
    }

    public static void main(String args[]) throws Exception {
        File index = Files.createTempDirectory("index").toFile();
        try(Stream<Path> fileList = Files.list(Paths.get(args[0]))) {
            for(Path docsPath: (Iterable<Path>) fileList::iterator) {
                Entrypoint.entrypoint(docsPath);
            }
        }
    }
}