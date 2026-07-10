import java.io.File;
import java.nio.file.*;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Stream;
import java.util.stream.Collectors;

import org.antlr.v4.automata.ATNPrinter;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.LexerInterpreter;
import org.antlr.v4.runtime.ParserInterpreter;
import org.antlr.v4.runtime.ParserRuleContext;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import org.antlr.v4.Tool;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.LexerGrammar;

public class Entrypoint {

    private static long processed = 0;
    private static long total = 0;

    public static void entrypoint(Path inputDirectory) throws Exception {

        System.out.println(String.format("[%d/%d]: %s", processed, total, inputDirectory.toString()));

        try (Stream<Path> grammarFilesStream = Files.find(inputDirectory, Integer.MAX_VALUE, (path, attr) -> attr.isRegularFile() && path.toString().toLowerCase().endsWith(".g4"))) {
            Set<Path> grammarFiles = grammarFilesStream.collect(Collectors.toSet());
            if(grammarFiles.size() == 1) {
                Path grammarFile = grammarFiles.iterator().next();
                parse(inputDirectory, grammarFile);
            } else {
                Path lexerFile = grammarFiles.stream().filter(file -> file.toString().endsWith("Lexer.g4")).findFirst().orElse(null);
                Path parserFile = grammarFiles.stream().filter(file -> file.toString().endsWith("Parser.g4")).findFirst().orElse(null);
                parse(inputDirectory, lexerFile, parserFile);
            }
        } catch (Throwable exc) {
            exc.printStackTrace(System.err);
        } finally {
            processed += 1;
        }
    }

    public static void parse(Path inputDirectory,
                             Path lexerGrammarFileName,
                             Path parserGrammarFileName)
            throws Exception
    {
        System.out.println(String.format("======== %s ========\n", parserGrammarFileName.toString()));
        long start = System.nanoTime();

        Tool tool = new Tool();

        final LexerGrammar lexerGrammar = new LexerGrammar(Files.readString(lexerGrammarFileName));
        lexerGrammar.fileName = lexerGrammarFileName.getFileName().toString();

        final Grammar parserGrammar = new Grammar(Files.readString(parserGrammarFileName), lexerGrammar);
        parserGrammar.fileName = parserGrammarFileName.getFileName().toString();
        tool.process(parserGrammar, true);

        parserGrammar.implicitLexer = lexerGrammar;

        long end = System.nanoTime();
        double seconds = (end - start) * 1e-9d;
        System.out.println(String.format("Grammar processing %s: %.2fs\n", parserGrammarFileName.toString(), seconds));

        parseExamples(inputDirectory, parserGrammar);
    }

    public static void parse(Path inputDirectory,
                             Path grammarFileName)
            throws Exception
    {
        System.out.println(String.format("======== %s ========\n", grammarFileName.toString()));
        long start = System.nanoTime();
        Tool tool = new Tool();

        org.antlr.v4.tool.ast.GrammarRootAST ast = tool.parseGrammarFromString(Files.readString(grammarFileName));
        Grammar parserGrammar = tool.createGrammar(ast);
        parserGrammar.fileName = grammarFileName.getFileName().toString();
        tool.process(parserGrammar, true);

//        ATNPrinter printer = new ATNPrinter(parserGrammar, parserGrammar.getATN().ruleToStartState[0]);
//        printer.asString();
        long end = System.nanoTime();
        double seconds = (end - start) * 1e-9d;
        System.out.println(String.format("Grammar processing %s: %.2fs\n", grammarFileName.toString(), seconds));

        parseExamples(inputDirectory, parserGrammar);
    }

    public static void parseExamples(Path inputDirectory, Grammar parserGrammar) throws Exception {
        try(Stream<Path> examples = Files.find(inputDirectory.resolve("examples"), Integer.MAX_VALUE, (path, attr) -> attr.isRegularFile())) {
            for(Path example: (Iterable<Path>) examples::iterator) {
                try {
                    long start = System.nanoTime();
                    CharStream input = CharStreams.fromPath(example);
                    LexerInterpreter lexEngine = parserGrammar.implicitLexer.createLexerInterpreter(input);
                    lexEngine.removeErrorListeners();
                    CommonTokenStream tokens = new CommonTokenStream(lexEngine);
                    ParserInterpreter parser = parserGrammar.createParserInterpreter(tokens);
                    parser.removeErrorListeners();
                    ParseTree tree = parser.parse(0);

                    ParseTreeWalker walker = new ParseTreeWalker();
                    walker.walk(new ParseTreeListener() {
                        long size = 0;

                        @Override
                        public void enterEveryRule(ParserRuleContext ctx) {}

                        @Override
                        public void exitEveryRule(ParserRuleContext ctx) {}

                        @Override
                        public void visitErrorNode(ErrorNode node) { size += 1; }

                        @Override
                        public void visitTerminal(TerminalNode node) { size += 1; }
                    }, tree);

                    long end = System.nanoTime();
                    double seconds = (end - start) * 1e-9d;
                    System.out.println(String.format("%s: %.2fs\n", example.toString(), seconds));
                } catch (Throwable exc) {
                    System.err.println(String.format("%s: %s\n", example.toString(), exc.toString()));
                }
            }
        }
    }

    public static void main(String args[]) throws Exception {
        try (Stream<Path> stream = Files.walk(Paths.get(args[0]), 1)) { Entrypoint.total = stream.filter(Files::isRegularFile).count(); }
        try(Stream<Path> files = Files.walk(Paths.get(args[0]), 1)) {
            for(Path inputDirectory: (Iterable<Path>) files.sorted().filter(Files::isDirectory)::iterator) {
                Entrypoint.entrypoint(inputDirectory);
            }
        }
    }
}