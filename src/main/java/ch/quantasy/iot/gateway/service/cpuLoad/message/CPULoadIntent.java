/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.quantasy.iot.gateway.service.cpuLoad.message;

import ch.quantasy.mqtt.gateway.client.message.AnIntent;
import ch.quantasy.mqtt.gateway.client.message.annotations.Period;

/**
 *
 * @author reto
 */
public class CPULoadIntent extends AnIntent{
    @Period(from = 0,to = 10000)
    public long callbackPeriod;

    public CPULoadIntent() {
    }

    public CPULoadIntent(long callbackPeriod) {
        this.callbackPeriod = callbackPeriod;
    }
    
    
    
   
}
