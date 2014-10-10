package demo1;

import sun.jvmstat.monitor.MonitoredHost;
import sun.jvmstat.monitor.MonitoredVm;
import sun.jvmstat.monitor.VmIdentifier;

public class JavaProcesses {

    public static void main(String[] args) throws Exception {
        MonitoredHost host = MonitoredHost.getMonitoredHost((String) null);
        for (Integer vmId : host.activeVms()) {
            MonitoredVm vm = host.getMonitoredVm(new VmIdentifier(vmId.toString()));
            Object javaCommand = vm.findByName("sun.rt.javaCommand").getValue();
            System.out.println(vmId + ": " + javaCommand);

            vm.findByPattern(".+").forEach(monitor -> {
                System.out.println(monitor.getName() + " = " + monitor.getValue());
            });
        }
    }
}
