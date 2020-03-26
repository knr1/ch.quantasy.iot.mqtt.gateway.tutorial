/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.quantasy.iot.gateway.service.memory.message;

import ch.quantasy.mdservice.message.AnIntent;
import ch.quantasy.mdservice.message.annotations.Period;

/**
 *
 * @author reto
 */
public class MemoryUsageIntent extends AnIntent {

    @Period
    public Long memoryUsageCallbackPeriod;

    public MemoryUsageIntent(Long memoryUsageCallbackPeriod) {
        this.memoryUsageCallbackPeriod = memoryUsageCallbackPeriod;
    }

    public MemoryUsageIntent() {
    }

}
