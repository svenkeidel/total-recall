import java.io.File;
import java.nio.file.Files;
import java.io.IOException;

import java.io.ByteArrayInputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

import jazzer.InputParser;

public class Entrypoint {
    public static void entrypoint(String xml) {
        try {
            System.setProperty("javax.xml.parsers.DocumentBuilderFactory",
                               "org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(true);
            DocumentBuilder parser = factory.newDocumentBuilder();
            parser.setErrorHandler(new ErrorHandler() {
                @Override
                public void warning(SAXParseException e) {}
                @Override
                public void error(SAXParseException e) {}
                @Override
                public void fatalError(SAXParseException e) {}
            });
            parser.parse(new ByteArrayInputStream(xml.getBytes()));
        } catch (Throwable exc) {
            // Ignore exceptions
            exc.printStackTrace(System.out);
        }
    }
    public static void recurseDirectories(File path) throws IOException {
        for(File inputFile: path.listFiles()) {
            if(inputFile.isFile()) {
                String input = InputParser.parseString(Files.readAllBytes(inputFile.toPath()));
                Entrypoint.entrypoint(input);
            } else {
                recurseDirectories(inputFile);
            }
        }
    }
    public static void main(String args[]) throws IOException {
        recurseDirectories(new File(args[0]));
    }
}