/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.quantasy.iot.gateway.service.cpuLoad.message;

import ch.quantasy.mqtt.gateway.client.message.AnEvent;
import ch.quantasy.mqtt.gateway.client.message.annotations.Range;

/**
 *
 * @author reto
 */
public class CPULoadEvent extends AnEvent{
    @Range(from = 0,to = 100)
    public int percent;

    private CPULoadEvent() {
    }

    public CPULoadEvent(int percent) {
        this.percent = percent;
    }
    
    
}
