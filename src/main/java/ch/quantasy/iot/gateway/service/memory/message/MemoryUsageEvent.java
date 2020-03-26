/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.quantasy.iot.gateway.service.memory.message;

import ch.quantasy.mdservice.message.AnEvent;
import ch.quantasy.mdservice.message.annotations.Range;

/**
 *
 * @author reto
 */
public class MemoryUsageEvent extends AnEvent {

    @Range(from = 0)
    public long total;
    @Range(from = 0)
    public long free;

    public MemoryUsageEvent(long total, long free) {
        this.total = total;
        this.free = free;
    }

    private MemoryUsageEvent() {
    }
    
   

}
