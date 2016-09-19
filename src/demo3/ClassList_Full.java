package demo3;

import sun.jvm.hotspot.runtime.VM;
import sun.jvm.hotspot.tools.Tool;

public class ClassList_Full extends Tool {

    @Override
    public void run() {
        VM.getVM().getSystemDictionary().classesDo((klass, loader) -> {
            System.out.println(klass.getName().asString());
            if (loader != null) {
                System.out.println(" - " + loader.getKlass().getName().asString());
            }
        });
    }

    public static void main(String[] args) {
        new ClassList_Full().execute(new String[]{""});
    }
}
