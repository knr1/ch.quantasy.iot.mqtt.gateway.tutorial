/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.quantasy.iot.memory;

/**
 *
 * @author reto
 */
public interface MemoryUsageSensorCallback {
    public void memoryUsageChanged(PhysicalMemory memory);
}
