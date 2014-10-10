package demo3;

import sun.jvm.hotspot.memory.SystemDictionary;
import sun.jvm.hotspot.oops.InstanceKlass;
import sun.jvm.hotspot.oops.OopField;
import sun.jvm.hotspot.runtime.VM;
import sun.jvm.hotspot.tools.Tool;

public class InternedStrings extends Tool {

    @Override
    public void run() {
        SystemDictionary dict = VM.getVM().getSystemDictionary();
        InstanceKlass stringKlass = (InstanceKlass) dict.find("java/lang/String", null, null);
        OopField valueField = (OopField) stringKlass.findField("value", "[C");

        long[] stats = new long[2];
        VM.getVM().getStringTable().stringsDo(s -> {
            s.printValueOn(System.out);
            System.out.println();
            stats[0]++;
            stats[1] += s.getObjectSize() + valueField.getValue(s).getObjectSize();
        });
        System.out.printf("%d string with total size %d\n", stats[0], stats[1]);
    }

    public static void main(String[] args) {
        new InternedStrings().execute(args);
    }
}
