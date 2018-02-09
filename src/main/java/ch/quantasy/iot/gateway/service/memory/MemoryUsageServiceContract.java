/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.quantasy.iot.gateway.service.memory;

import ch.quantasy.iot.gateway.service.memory.message.MemoryUsageEvent;
import ch.quantasy.iot.gateway.service.memory.message.MemoryUsageIntent;
import ch.quantasy.iot.gateway.service.memory.message.MemoryUsageStatus;
import ch.quantasy.mqtt.gateway.client.contract.AyamlServiceContract;
import java.util.Map;

/**
 *
 * @author reto
 */
public class MemoryUsageServiceContract extends AyamlServiceContract {

    private final String MEMORY;
    private final String USAGE;
    private final String CALLBACK_PERIOD;
    public final String STATUS_MEMORY_USAGE_CALLBACK_PERIOD;
    public final String EVENT_MEMORY_USAGE;

    public MemoryUsageServiceContract(String instanceID) {
        super("Tutorial", "MemoryUsage", instanceID);

        MEMORY = "memory";
        USAGE = "usage";
        CALLBACK_PERIOD = "callbackPeriod";
        STATUS_MEMORY_USAGE_CALLBACK_PERIOD = STATUS + "/" + MEMORY + "/" + USAGE + "/" + CALLBACK_PERIOD;
        EVENT_MEMORY_USAGE = EVENT + "/" + MEMORY + "/" + USAGE;
    }

    @Override
    public void setMessageTopics(Map messageTopicMap) {
        messageTopicMap.put(EVENT_MEMORY_USAGE, MemoryUsageEvent.class);
        messageTopicMap.put(INTENT, MemoryUsageIntent.class);
        messageTopicMap.put(STATUS_MEMORY_USAGE_CALLBACK_PERIOD, MemoryUsageStatus.class);
    }
}
