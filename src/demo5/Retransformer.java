package demo5;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Retransformer {

    public static void agentmain(String arg, Instrumentation instr) throws Exception {
        Class oldClass = Class.forName("SwingSet2$AboutAction");

        String classFile = "out/production/serviceability/SwingSet2$AboutAction.class";
        byte[] newClassData = Files.readAllBytes(Paths.get(classFile));

        instr.redefineClasses(new ClassDefinition(oldClass, newClassData));
    }
}
