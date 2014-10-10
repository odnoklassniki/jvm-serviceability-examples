package demo5;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Retransformer {

    public static void agentmain(String arg, Instrumentation instr) throws Exception {
        String[] args = arg.split(" ");
        Class oldClass = Class.forName(args[0]);
        byte[] newClassData = Files.readAllBytes(Paths.get(args[1]));
        instr.redefineClasses(new ClassDefinition(oldClass, newClassData));
    }
}
