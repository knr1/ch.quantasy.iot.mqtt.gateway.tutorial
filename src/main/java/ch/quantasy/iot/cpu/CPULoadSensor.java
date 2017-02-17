/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.quantasy.iot.cpu;

import com.sun.management.OperatingSystemMXBean;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Timer;
import java.util.TimerTask;
import javax.management.MBeanServerConnection;

/**
 *
 * @author reto
 */
public class CPULoadSensor {

    private Timer timer;
    private final MBeanServerConnection mBeanServerConnection;
    private final OperatingSystemMXBean osmxbean;
    private long cpuLoadCallbackPeriod;
    private int latestCPULoad;

    private final CPULoadSensorCallback callback;

    public CPULoadSensor(CPULoadSensorCallback callback) throws IOException {
        this.callback = callback;
        mBeanServerConnection = ManagementFactory.getPlatformMBeanServer();
        osmxbean = ManagementFactory.newPlatformMXBeanProxy(
                mBeanServerConnection, ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME, OperatingSystemMXBean.class);
    }

    public void setCPULoadCallbackPeriod(long milliseconds) {
        this.cpuLoadCallbackPeriod = milliseconds;
        cpuScheduler();
    }

    public long getCpuLoadCallbackPeriod() {
        return cpuLoadCallbackPeriod;
    }

    private void cpuScheduler() {
        if (this.timer != null) {
            this.timer.cancel();
        }
        if (this.cpuLoadCallbackPeriod < 1) {
            return;
        }
        this.timer = new Timer(true);
        this.timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    int currentCPULoad = ((int) (osmxbean.getSystemCpuLoad() * 100 + 0.5));
                    if (currentCPULoad == latestCPULoad) {
                        return;
                    }
                    latestCPULoad = currentCPULoad;
                    callback.cpuLoadChanged(latestCPULoad);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }, 0, this.cpuLoadCallbackPeriod);
    }
}
