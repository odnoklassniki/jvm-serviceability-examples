package demo4;

import sun.jvmstat.monitor.Monitor;
import sun.jvmstat.monitor.MonitoredHost;
import sun.jvmstat.monitor.MonitoredVm;
import sun.jvmstat.monitor.VmIdentifier;

public class ClassCounters {

    private static MonitoredVm findVm(String name) throws Exception {
        MonitoredHost host = MonitoredHost.getMonitoredHost((String) null);
        for (Integer vmId : host.activeVms()) {
            MonitoredVm vm = host.getMonitoredVm(new VmIdentifier(vmId.toString()));
            String cmd = (String) vm.findByName("sun.rt.javaCommand").getValue();
            if (cmd.endsWith(name)) {
                return vm;
            }
            vm.detach();
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        MonitoredVm vm = findVm("ExpressionEval");

        Monitor loaded = vm.findByName("java.cls.loadedClasses");
        Monitor unloaded = vm.findByName("java.cls.unloadedClasses");
        Monitor bytes = vm.findByName("sun.cls.loadedBytes");
        Monitor time = vm.findByName("sun.cls.time");

        for (;;) {
            System.out.printf("%6d  %6d  %8d  %.3f\n",
                    loaded.getValue(), unloaded.getValue(),
                    bytes.getValue(), (long) time.getValue() / 1000000.0);
            Thread.sleep(500);
        }
    }
}
