package demo1;

import sun.jvmstat.monitor.Monitor;
import sun.jvmstat.monitor.MonitoredHost;
import sun.jvmstat.monitor.MonitoredVm;
import sun.jvmstat.monitor.VmIdentifier;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PerfCounterProfiler {

    public static void main(String[] args) throws Exception {
        MonitoredHost host = MonitoredHost.getMonitoredHost((String) null);
        MonitoredVm vm = host.getMonitoredVm(new VmIdentifier(""));

        List<Monitor> monitors = vm.findByPattern(".*");
        Map<Monitor, Object> values = monitors.stream()
                .collect(Collectors.toMap(Function.identity(), Monitor::getValue));

        while (true) {
            Thread.sleep(1000);
            System.out.println(" --- ");

            monitors.forEach(m -> {
                Object newValue = m.getValue();
                Object oldValue = values.put(m, newValue);
                if (!oldValue.equals(newValue)) {
                    System.out.println(m.getName() + " : " + newValue);
                }
            });
        }
    }
}
