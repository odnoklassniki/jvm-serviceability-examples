package demo3;

import sun.jvm.hotspot.oops.*;
import sun.jvm.hotspot.runtime.VM;
import sun.jvm.hotspot.tools.Tool;

public class KeyScanner extends Tool {

    @Override
    public void run() {
        Klass privateKeyKlass = VM.getVM().getSystemDictionary()
                .find("java/security/PrivateKey", null, null);

        VM.getVM().getObjectHeap().iterateObjectsOfKlass(new DefaultHeapVisitor() {
            @Override
            public boolean doObj(Oop oop) {
                InstanceKlass holder = (InstanceKlass) oop.getKlass();
                OopField field = (OopField) holder.findField("key", "[B");
                print((TypeArray) field.getValue(oop));
                return false;
            }
        }, privateKeyKlass);
    }

    private void print(TypeArray array) {
        long length = array.getLength();
        for (long i = 0; i < length; i++) {
            System.out.printf("%02x", array.getByteAt(i));
        }
        System.out.println();
    }

    public static void main(String[] args) {
        new KeyScanner().execute(new String[]{""});
    }
}
