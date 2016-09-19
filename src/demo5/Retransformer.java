package demo5;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Retransformer {
    private static final String CLASSPATH = "out/production/serviceability/";

    public static void agentmain(String arg, Instrumentation instr) throws Exception {
        Class oldClass = Class.forName("java2d.demos.Arcs_Curves.Ellipses");
        String newFile = CLASSPATH + "java2d/demos/Arcs_Curves/Ellipses.class";

        byte[] newClassData = Files.readAllBytes(Paths.get(newFile));
        instr.redefineClasses(new ClassDefinition(oldClass, newClassData));
    }
}
