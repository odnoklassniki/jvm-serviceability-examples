package demo3;

import sun.jvm.hotspot.gc_implementation.parallelScavenge.PSOldGen;
import sun.jvm.hotspot.gc_implementation.parallelScavenge.ParallelScavengeHeap;
import sun.jvm.hotspot.gc_interface.CollectedHeap;
import sun.jvm.hotspot.oops.DefaultHeapVisitor;
import sun.jvm.hotspot.oops.Klass;
import sun.jvm.hotspot.oops.Oop;
import sun.jvm.hotspot.runtime.VM;
import sun.jvm.hotspot.tools.Tool;

public class OldGenTool extends Tool {

    @Override
    public void run() {
        CollectedHeap heap = VM.getVM().getUniverse().heap();
        PSOldGen oldGen = ((ParallelScavengeHeap) heap).oldGen();

        Klass klass = VM.getVM().getSystemDictionary().find("java/lang/String", null, null);

        VM.getVM().getObjectHeap().iterateObjectsOfKlass(new DefaultHeapVisitor() {
            @Override
            public boolean doObj(Oop oop) {
                if (oldGen.isIn(oop.getHandle())) {
                    oop.printValue();
                    System.out.println();
                }
                return false;
            }
        }, klass);
    }

    public static void main(String[] args) {
        new OldGenTool().execute(new String[]{""});
    }
}
