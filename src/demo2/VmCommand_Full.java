package demo2;

import com.sun.tools.attach.VirtualMachine;
import sun.tools.attach.HotSpotVirtualMachine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class VmCommand_Full {

    private static void readResponse(InputStream in) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
            br.lines().forEach(System.out::println);
        }
    }

    public static void main(String[] args) throws Exception {
        HotSpotVirtualMachine vm = (HotSpotVirtualMachine) VirtualMachine.attach("");
        Properties props = vm.getSystemProperties();
        readResponse(vm.printFlag("PrintGC"));
        readResponse(vm.setFlag("PrintGC", "1"));
        readResponse(vm.executeJCmd("GC.run"));
    }
}
