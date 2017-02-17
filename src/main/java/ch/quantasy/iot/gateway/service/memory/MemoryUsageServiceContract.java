/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.quantasy.iot.gateway.service.memory;

import ch.quantasy.mqtt.gateway.client.ClientContract;

/**
 *
 * @author reto
 */
public class MemoryUsageServiceContract extends ClientContract {

    private final String MEMORY;
    private final String USAGE;
    private final String CALLBACK_PERIOD;
    public final String INTENT_MEMORY_USAGE_CALLBACK_PERIOD;
    public final String STATUS_MEMORY_USAGE_CALLBACK_PERIOD;
    public final String EVENT_MEMORY_USAGE;

    public MemoryUsageServiceContract(String instanceID) {
        super("Tutorial", "MemoryUsage", instanceID);

        MEMORY="memory";
        USAGE="usage";
        CALLBACK_PERIOD="callbackPeriod";
        INTENT_MEMORY_USAGE_CALLBACK_PERIOD = INTENT + "/"+MEMORY+"/"+USAGE+"/"+CALLBACK_PERIOD;
        STATUS_MEMORY_USAGE_CALLBACK_PERIOD = STATUS + "/"+MEMORY+"/"+USAGE+"/"+CALLBACK_PERIOD;
        EVENT_MEMORY_USAGE=EVENT +"/"+MEMORY+"/"+USAGE;
        
        
    }

}
