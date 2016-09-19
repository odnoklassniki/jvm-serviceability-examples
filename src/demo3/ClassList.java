package demo3;

import sun.jvm.hotspot.runtime.VM;
import sun.jvm.hotspot.tools.Tool;

public class ClassList extends Tool {

    @Override
    public void run() {
        VM.getVM();
    }

    public static void main(String[] args) {
        new ClassList().execute(new String[]{""});
    }
}
