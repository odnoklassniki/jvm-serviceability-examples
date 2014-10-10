package demo2;

import com.sun.tools.attach.VirtualMachine;
import sun.tools.attach.HotSpotVirtualMachine;

import java.io.IOException;
import java.io.InputStream;

public class VmCommand {

    private static void readResponse(InputStream input) throws IOException {
        try (InputStream is = input) {
            byte[] buffer = new byte[8000];
            for (int bytes; (bytes = is.read(buffer)) > 0; ) {
                System.out.write(buffer, 0, bytes);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        HotSpotVirtualMachine vm = (HotSpotVirtualMachine) VirtualMachine.attach(args[0]);
        readResponse(vm.remoteDataDump());
        readResponse(vm.heapHisto());
        readResponse(vm.setFlag("PrintGC", "1"));
        readResponse(vm.setFlag("PrintGCDetails", "1"));
        readResponse(vm.executeJCmd("GC.run"));
    }
}
