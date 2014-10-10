package demo5;

import com.sun.tools.attach.VirtualMachine;

import java.nio.file.Paths;

public class InjectAgent {

    public static void main(String[] args) throws Exception {
        String agentJar = Paths.get("retransformer.jar").toAbsolutePath().toString();
        VirtualMachine vm = VirtualMachine.attach(args[0]);
        vm.loadAgent(agentJar, args[1] + ' ' + args[2]);
        vm.detach();
    }
}
