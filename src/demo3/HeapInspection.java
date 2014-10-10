package demo3;

import sun.jvm.hotspot.memory.SystemDictionary;
import sun.jvm.hotspot.oops.DefaultHeapVisitor;
import sun.jvm.hotspot.oops.InstanceKlass;
import sun.jvm.hotspot.oops.IntField;
import sun.jvm.hotspot.oops.Oop;
import sun.jvm.hotspot.oops.OopField;
import sun.jvm.hotspot.runtime.VM;
import sun.jvm.hotspot.tools.Tool;

public class HeapInspection extends Tool {

    @Override
    public void run() {
        SystemDictionary dict = VM.getVM().getSystemDictionary();

        InstanceKlass bufferedImageKlass = (InstanceKlass) dict.find("java/awt/image/BufferedImage", null, null);
        OopField raster = (OopField) bufferedImageKlass.findField("raster", "Ljava/awt/image/WritableRaster;");

        InstanceKlass rasterKlass = (InstanceKlass) dict.find("java/awt/image/Raster", null, null);
        IntField width = (IntField) rasterKlass.findField("width", "I");
        IntField height = (IntField) rasterKlass.findField("height", "I");

        VM.getVM().getObjectHeap().iterateObjectsOfKlass(new DefaultHeapVisitor() {
            int count;

            @Override
            public boolean doObj(Oop obj) {
                Oop rasterObj = raster.getValue(obj);
                int w = width.getValue(rasterObj);
                int h = height.getValue(rasterObj);
                System.out.printf("BufferedImage %d: %d x %d\n", ++count, w, h);
                return false;
            }
        }, bufferedImageKlass);
    }

    public static void main(String[] args) {
        new HeapInspection().execute(args);
    }
}
