package demo5;

import com.sun.tools.attach.VirtualMachine;

import java.nio.file.Paths;

public class InjectAgent {

    public static void main(String[] args) throws Exception {
        String agentJar = Paths.get("lib/retransformer.jar").toAbsolutePath().toString();

        VirtualMachine vm = VirtualMachine.attach("");
        vm.loadAgent(agentJar);
        vm.detach();
    }
}
