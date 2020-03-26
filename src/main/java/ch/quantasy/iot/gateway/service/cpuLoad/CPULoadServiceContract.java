/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.quantasy.iot.gateway.service.cpuLoad;

import ch.quantasy.iot.gateway.service.cpuLoad.message.CPULoadEvent;
import ch.quantasy.iot.gateway.service.cpuLoad.message.CPULoadIntent;
import ch.quantasy.iot.gateway.service.cpuLoad.message.CPULoadStatus;
import ch.quantasy.mdservice.message.Message;
import ch.quantasy.mdsmqtt.gateway.client.contract.AyamlServiceContract;
import java.util.Map;

/**
 *
 * @author reto
 */
public class CPULoadServiceContract extends AyamlServiceContract {

    private final String CPU;
    private final String LOAD;
    private final String CALLBACK_PERIOD;
    public final String STATUS_CPU_LOAD_CALLBACK_PERIOD;
    public final String EVENT_CPU_LOAD;

    public CPULoadServiceContract(String instanceID) {
        super("Tutorial", "CPULoad", instanceID);

        CPU = "cpu";
        LOAD = "load";
        CALLBACK_PERIOD = "callbackPeriod";
        STATUS_CPU_LOAD_CALLBACK_PERIOD = STATUS + "/" + CPU + "/" + LOAD + "/" + CALLBACK_PERIOD;
        EVENT_CPU_LOAD = EVENT + "/" + CPU + "/" + LOAD;

    }

    @Override
    public void setMessageTopics(Map<String, Class<? extends Message>> messageTopicMap) {
        messageTopicMap.put(INTENT, CPULoadIntent.class);
        messageTopicMap.put(STATUS_CPU_LOAD_CALLBACK_PERIOD, CPULoadStatus.class);
        messageTopicMap.put(EVENT_CPU_LOAD, CPULoadEvent.class);
    }

}
