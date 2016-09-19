package demo4;

import sun.jvmstat.monitor.*;

import java.util.NoSuchElementException;

public class ClassCounters {

    private static MonitoredVm findVm(String name) throws Exception {
        MonitoredHost host = MonitoredHost.getMonitoredHost((String) null);
        for (Integer vmId : host.activeVms()) {
            MonitoredVm vm = host.getMonitoredVm(new VmIdentifier(vmId.toString()));
            if (MonitoredVmUtil.commandLine(vm).endsWith(name)) {
                return vm;
            }
            vm.detach();
        }
        throw new NoSuchElementException();
    }

    public static void main(String[] args) throws Exception {
        MonitoredVm vm = findVm("Eval");

        Monitor loaded   = vm.findByName("java.cls.loadedClasses");
        Monitor unloaded = vm.findByName("java.cls.unloadedClasses");
        Monitor bytes    = vm.findByName("sun.cls.loadedBytes");
        Monitor time     = vm.findByName("sun.cls.time");

        while (true) {
            System.out.printf("loaded=%s  unloaded=%s  bytes=%s  time=%.3f\n",
                    loaded.getValue(), unloaded.getValue(),
                    bytes.getValue(), (long) time.getValue() / 1000000.0);
            Thread.sleep(500);
        }
    }
}
