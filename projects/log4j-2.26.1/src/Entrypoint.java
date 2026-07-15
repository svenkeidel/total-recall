import org.apache.logging.log4j.MarkerManager;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.message.*;

import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.appender.*;
import org.apache.logging.log4j.core.layout.*;
import org.apache.logging.log4j.core.config.*;
import org.apache.logging.log4j.core.config.builder.api.*;
import org.apache.logging.log4j.core.config.builder.impl.*;

import org.apache.logging.log4j.layout.template.json.*;

import java.util.*;
import java.nio.charset.StandardCharsets;
public class Entrypoint {
    public static void entrypoint() throws Exception {
        for(LoggerContext loggerContext: createLoggerContexts()) {
            logMessages(loggerContext);
        }
    }

    private static List<String> createPatternConverters() {
        List<String> converters = new ArrayList<>();

        converters.add("%class{10}");
        converters.add("%class{.}");
        converters.add("%class{1.2.*}");

        converters.add("%date{DEFAULT}");
        converters.add("%date{DEFAULT_MICROS}");
        converters.add("%date{DEFAULT_NANOS}");
        converters.add("%date{ISO8601}");
        converters.add("%date{ISO8601_BASIC}");
        converters.add("%date{ISO8601_OFFSET_DATE_TIME_HH}");
        converters.add("%date{ISO8601_OFFSET_DATE_TIME_HHMM}");
        converters.add("%date{ISO8601_OFFSET_DATE_TIME_HHCMM}");
        converters.add("%date{ABSOLUTE}");
        converters.add("%date{ABSOLUTE_MICROS}");
        converters.add("%date{ABSOLUTE_NANOS}");
        converters.add("%date{DATE}");
        converters.add("%date{COMPACT}");
        converters.add("%date{UNIX}");
        converters.add("%date{UNIX_MILLIS}");
        converters.add("%date{HH:mm:ss,SSS}");
        converters.add("%date{yyyy-mm-dd’T’HH:mm:ss.SSS’Z'}{UTC}");
        converters.add("%date{dd-MMMM-yyyy}{UTC}{de-DE}");

        converters.add("%encode{%msg}{HTML}");
        converters.add("%encode{%msg}{XML}");
        converters.add("%encode{%msg}{JSON}");
        converters.add("%encode{%msg}{CRLF}");

        converters.add("%endOfBatch");

        converters.add("%equals{[%marker]}{[]}{}");

        converters.add("%exception{none}{filters(java.io)}{separator(,)}{suffix(!)}");
        converters.add("%exception{short}{filters(java.io)}{separator(,)}{suffix(!)}");
        converters.add("%exception{5}{filters(java.io)}{separator(,)}{suffix(!)}");
        converters.add("%exception{full}{filters(java.io)}{separator(,)}{suffix(!)}");

        converters.add("%rException{none}{filters(java.io)}{separator(,)}{suffix(!)}");
        converters.add("%rException{short}{filters(java.io)}{separator(,)}{suffix(!)}");
        converters.add("%rException{5}{filters(java.io)}{separator(,)}{suffix(!)}");
        converters.add("%rException{full}{filters(java.io)}{separator(,)}{suffix(!)}");

        converters.add("%xException{none}{filters(java.io)}{separator(,)}{suffix(!)}");
        converters.add("%xException{short}{filters(java.io)}{separator(,)}{suffix(!)}");
        converters.add("%xException{5}{filters(java.io)}{separator(,)}{suffix(!)}");
        converters.add("%xException{full}{filters(java.io)}{separator(,)}{suffix(!)}");

        converters.add("%file");

        converters.add("%fqcn");

        converters.add("%highlight{%level{WARN=Warning, DEBUG=Debug, ERROR=Error, TRACE=Trace, INFO=Info}}{FATAL=white, ERROR=red, WARN=blue, INFO=black, DEBUG=green, TRACE=magenta, length=5, lowerCase=true}");

        converters.add("%line");

        converters.add("%location");

        converters.add("%logger{10}");
        converters.add("%logger{.}");
        converters.add("%logger{1.2.*}");

        converters.add("%marker");
        converters.add("%markerSimpleName");

        converters.add("%map{boolean}");
        converters.add("%maxLen{%msg}{10}");

        converters.add("%message{ansi}{JAVA}");
        converters.add("%message{ansi}{JAVA_UNQOUTED}");
        converters.add("%message{ansi}{XML}");
        converters.add("%message{ansi}{JSON}");

        converters.add("%method");

        converters.add("%nano");

        converters.add("%variablesNotEmpty{%marker}");

        converters.add("%processId{-1}");

        converters.add("%relative");

        converters.add("%repeat{*}{3}");

        converters.add("%replace{%msg}{1}{2}");

        converters.add("%sequenceNumber");

        converters.add("%style{%sequenceNumber}{black}");
        converters.add("%style{%sequenceNumber}{red}");
        converters.add("%style{%sequenceNumber}{green}");
        converters.add("%style{%sequenceNumber}{yellow}");
        converters.add("%style{%sequenceNumber}{blue}");
        converters.add("%style{%sequenceNumber}{magenta}");
        converters.add("%style{%sequenceNumber}{cyan}");
        converters.add("%style{%sequenceNumber}{white}");

        converters.add("%style{%sequenceNumber}{bright_black}");
        converters.add("%style{%sequenceNumber}{bright_red}");
        converters.add("%style{%sequenceNumber}{bright_green}");
        converters.add("%style{%sequenceNumber}{bright_yellow}");
        converters.add("%style{%sequenceNumber}{bright_blue}");
        converters.add("%style{%sequenceNumber}{bright_magenta}");
        converters.add("%style{%sequenceNumber}{bright_cyan}");
        converters.add("%style{%sequenceNumber}{bright_white}");

        converters.add("%style{%sequenceNumber}{normal}");
        converters.add("%style{%sequenceNumber}{bold}");
        converters.add("%style{%sequenceNumber}{dim}");
        converters.add("%style{%sequenceNumber}{underline}");
        converters.add("%style{%sequenceNumber}{blink}");
        converters.add("%style{%sequenceNumber}{reverse}");
        converters.add("%style{%sequenceNumber}{hidden}");

        converters.add("%NDC");
        converters.add("%threadId");
        converters.add("%threadName");
        converters.add("%threadPriority");

        converters.add("%uuid{RANDOM}");
        converters.add("%uuid{TIME}");

        return converters;
    }

    private static List<Layout<String>> createLayouts() {
        List<Layout<String>> layouts = new ArrayList<>();

        List<String> converters = createPatternConverters();
        layouts.add(
                PatternLayout.newBuilder()
                        .setPattern(String.join(" ", converters))
                        .build()
        );

        layouts.add(CsvParameterLayout.createDefaultLayout());

        layouts.add(
                GelfLayout.newBuilder().build()
        );

        layouts.add(
                HtmlLayout.newBuilder().build()
        );

//        layouts.add(
//                JsonTemplateLayout.newBuilder().setFormat(JsonTemplateLayout.EventTemplateAdditionalField.Format.JSON).build()
//        );

        return layouts;
    }

    private static List<AbstractAppender> createAppender(Layout<String> layout) {
        List<AbstractAppender> appenders = new ArrayList<>();

        String appenderRef = "File-"+System.identityHashCode(layout);
        appenders.add(
                FileAppender.newBuilder()
                        .setName(appenderRef)
                        .setFileName(appenderRef+".log")
                        .setLayout(layout)
                        .build()
        );

        appenderRef = "RandomAccessFile-"+System.identityHashCode(layout);
        appenders.add(
                RandomAccessFileAppender.newBuilder()
                        .setName(appenderRef)
                        .setFileName(appenderRef+".log")
                        .setLayout(layout)
                        .build()
        );

        appenderRef = "MemoryMappedFile-"+System.identityHashCode(layout);
        appenders.add(
                MemoryMappedFileAppender.newBuilder()
                        .setName(appenderRef)
                        .setFileName(appenderRef+".log")
                        .setLayout(layout)
                        .build()
        );

        appenderRef = "Console-"+System.identityHashCode(layout);
        appenders.add(
                ConsoleAppender.newBuilder()
                        .setName(appenderRef)
                        .setLayout(layout)
                        .build()
        );

        return appenders;
    }

    public static class CustomConfiguration extends AbstractConfiguration {
        public CustomConfiguration(final LoggerContext loggerContext, final ConfigurationSource configSource) {
            super(loggerContext, configSource);
        }
    }

    private static List<LoggerContext> createLoggerContexts() {

        List<LoggerContext> loggerContexts = new ArrayList<>();

        ConfigurationSource nullSource = ConfigurationSource.NULL_SOURCE;
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        AbstractConfiguration configuration = new CustomConfiguration(context, nullSource);

        for(Layout<String> layout: createLayouts()) {
            for(AbstractAppender appender: createAppender(layout)) {
                configuration.addAppender(appender);
            }
        }

        Configurator.reconfigure(configuration);

        loggerContexts.add(context);

        return loggerContexts;
    }

    private static List<Marker> createMarkers() {
        List<Marker> markers = new ArrayList<Marker>();
        markers.add(MarkerManager.getMarker("Error"));
        markers.add(MarkerManager.getMarker("Message"));
        markers.add(MarkerManager.getMarker("SystemError"));
        markers.add(MarkerManager.getMarker("ApplicationError"));
        markers.get(2).addParents(markers.get(0), markers.get(1));
        markers.get(3).addParents(markers.get(0), markers.get(1));
        return markers;
    }

    private static List<Message> createMessages() {
        List<Message> messages = new ArrayList<>();
        messages.add(new SimpleMessage("SimpleMessage"));
        messages.add(new FormattedMessage("MessageFormattedMessage({0,number,integer},{1,time,full},{1,date,full},{2})", 1, new Date(), "foobar"));
        messages.add(new FormattedMessage("StringFormattedMessage(%d,%f)", 1, 2.0));
        messages.add(new FormattedMessage("ParameterizedMessage({},{},{})", 1, "arg2", true));
        messages.add(new ThreadDumpMessage("ThreadDumpMessage()"));
        messages.add(new ObjectArrayMessage(1, "arg2", false));

        Map<String,Object> map = new HashMap<>();
        map.put("boolean", true);
        map.put("int", (int)1);
        map.put("long", (long)2);
        map.put("float", (float)3.0);
        map.put("double", (double)4.0);
        map.put("boolean[]", new boolean[]{true, false});
        map.put("char[]", new char[]{'h', 'e', 'l', 'l', 'o'});
        map.put("byte[]", new byte[]{1, 2, 3});
        map.put("short[]", new short[]{1, 2, 3});
        map.put("int[]", new int[]{1, 2, 3});
        map.put("long[]", new long[]{1, 2, 3});
        map.put("float[]", new float[]{1, 2, 3});
        map.put("double[]", new double[]{1, 2, 3});
        map.put("Object[]", new Object[]{1, "foo", true});
        map.put("map", Map.<String,Object>of("foo", 1, "bar", 2));
        map.put("list", List.<Integer>of(1, 2, 3));
        map.put("set", Set.<Integer>of(1, 2, 3));
        messages.add(new MapMessage(map));
        messages.add(new StructuredDataMessage("id", "StructuredMessage()", "type"));
        return messages;
    }

    private static void logMessages(LoggerContext loggerContext) {
        Logger logger = loggerContext.getLogger("Entrypoint");
        debugMessages(logger);
        logger.error("error message");
        logger.fatal("fatal message");
        logger.info("info message");
        logger.trace("trace message");
        logger.warn("trace message");
    }

    private static void debugMessages(Logger logger) {
        for(Message message: createMessages()) {
            logger.debug(message);
            logger.debug(message, new RuntimeException("RuntimeException"));
            for(Marker marker: createMarkers()) {
                logger.debug(marker, message);
                logger.debug(marker, message, new java.io.IOException("IOException"));
            }
        }
    }

    public static void main(String[] args) throws Exception {
        entrypoint();
    }
}
