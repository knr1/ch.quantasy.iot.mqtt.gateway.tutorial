/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.quantasy.iot.memory;

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
public class MemoryUsageSensor {

    private Timer timer;
    private final MBeanServerConnection mBeanServerConnection;
    private final OperatingSystemMXBean osmxbean;
    private long cpuLoadCallbackPeriod;
    private PhysicalMemory latestMemoryUsage;

    private final MemoryUsageSensorCallback callback;

    public MemoryUsageSensor(MemoryUsageSensorCallback callback) throws IOException {
        this.callback = callback;
        mBeanServerConnection = ManagementFactory.getPlatformMBeanServer();
        osmxbean = ManagementFactory.newPlatformMXBeanProxy(
                mBeanServerConnection, ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME, OperatingSystemMXBean.class);
    }

    public void setMemoryUsageCallbackPeriod(long milliseconds) {
        this.cpuLoadCallbackPeriod = milliseconds;
        memoryUsageScheduler();
    }

    public long getMemoryUsageCallbackPeriod() {
        return cpuLoadCallbackPeriod;
    }

    private void memoryUsageScheduler() {
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
                    long totalPhysicalMemory = ((long) (osmxbean.getTotalPhysicalMemorySize()));
                    long freePhysicalMemory = ((long) (osmxbean.getFreePhysicalMemorySize()));
                    PhysicalMemory currentMemoryUsage = new PhysicalMemory(totalPhysicalMemory, freePhysicalMemory);
                    if (currentMemoryUsage.equals(latestMemoryUsage))  {
                        return;
                    }

                    latestMemoryUsage = currentMemoryUsage;
                    callback.memoryUsageChanged(latestMemoryUsage);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }, 0, this.cpuLoadCallbackPeriod);
    }
}
