package demo1;

import sun.jvmstat.monitor.MonitoredHost;

public class JavaProcesses {

    public static void main(String[] args) throws Exception {
        MonitoredHost host = MonitoredHost.getMonitoredHost((String) null);

        System.out.println(host.activeVms());
    }
}
