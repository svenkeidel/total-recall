import java.io.File;
import java.nio.file.Files;
import java.io.IOException;

import edu.umd.cs.findbugs.FindBugs2;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.Repository;

public class Entrypoint {
    public static void entrypoint(File jar) {
        try {
            FindBugs2.main(new String[] {
                    "-effort:max",
                    "-experimental",
                    "-auxclasspath",
                    //"repo/annotations-1.3.9.jar:repo/asm-3.1.jar:repo/asm-analysis-3.1.jar:repo/asm-commons-3.1.jar:repo/asm-tree-3.1.jar:repo/asm-util-3.1.jar:repo/asm-xml-3.1.jar:repo/bcel-1.3.9.jar:repo/commons-lang-2.4.jar:repo/dom4j-1.6.1.jar:repo/findbugs.jar:repo/icu4j-2.6.1.jar:repo/jFormatString-1.3.9.jar:repo/jaxen-1.1.1.jar:repo/jdom-1.0.jar:repo/jsr305-1.3.9.jar:repo/xalan-2.6.0.jar:repo/xercesImpl-2.6.2.jar:repo/xml-apis-1.0.b2.jar:repo/xmlParserAPIs-2.6.2.jar:repo/xom-1.0.jar:"+
                    "resources/findbugs/jdk6/charsets.jar:" +
                    "resources/findbugs/jdk6/compilefontconfig.jar:" +
                    "resources/findbugs/jdk6/dnsns.jar:" +
                    "resources/findbugs/jdk6/localedata.jar:" +
                    "resources/findbugs/jdk6/pulse-java.jar:" +
                    "resources/findbugs/jdk6/sunjce_provider.jar:" +
                    "resources/findbugs/jdk6/sunpkcs11.jar:" +
                    "resources/findbugs/jdk6/indicim.jar:" +
                    "resources/findbugs/jdk6/thaiim.jar:" +
                    "resources/findbugs/jdk6/javazic.jar:" +
                    "resources/findbugs/jdk6/jce.jar:" +
                    "resources/findbugs/jdk6/jsse.jar:" +
                    "resources/findbugs/jdk6/management-agent.jar:" +
                    "resources/findbugs/jdk6/resources.jar:" +
                    "resources/findbugs/jdk6/rhino.jar:" +
                    "resources/findbugs/jdk6/rt.jar:" +
                    "resources/findbugs/jdk6/US_export_policy.jar:" +
                    "resources/findbugs/jdk6/local_policy.jar:" +
                    "resources/findbugs/jdk6/dt.jar:" +
                    "resources/findbugs/jdk6/jconsole.jar:" +
                    "resources/findbugs/jdk6/sa-jdi.jar:" +
                    "resources/findbugs/jdk6/tools.jar",
                    jar.toString(),
            });
        } catch (Throwable exc) {
            // Ignore exceptions
            exc.printStackTrace(System.out);
        }
    }
    public static void recurseDirectories(File path) throws IOException {
        for(File inputFile: path.listFiles()) {
            if(inputFile.isFile()) {
                System.out.println(inputFile.toString());
                Entrypoint.entrypoint(inputFile);
            } else {
                recurseDirectories(inputFile);
            }
        }
    }

    /**
     * Findbugs loads tries to load classes from the executing JDK.
     * However, the class parsing library BCEL <= v6.0 is not capable to load classes of JDK 8.
     * Therefore, we replace the classes findbugs wants to load with classes from JDK 6.
     */
    public static void patchBCELClasses() throws Throwable {
        String[] classes = new String[] {"java.io.Serializable", "java.util.Map", "java.util.Collection", "java.util.Comparator"};
        for(String clazz : classes) {
            ClassParser parser = new ClassParser("/resources/findbugs/jdk6/rt.jar", clazz.replace('.', '/')+".class");
            Repository.addClass(parser.parse());
        }
    }

    public static void main(String args[]) throws Throwable {
        patchBCELClasses();
        recurseDirectories(new File(args[0]));
    }
}