/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.quantasy.iot.gateway.service.memory;

import ch.quantasy.iot.memory.MemoryUsageSensor;
import ch.quantasy.iot.memory.MemoryUsageSensorCallback;
import ch.quantasy.iot.memory.PhysicalMemory;
import ch.quantasy.mqtt.gateway.client.GatewayClient;
import ch.quantasy.mqtt.gateway.client.GCEvent;
import java.io.IOException;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.paho.client.mqttv3.MqttException;

/**
 *
 * @author reto
 */
public class MemoryUsageService extends GatewayClient<MemoryUsageServiceContract> implements MemoryUsageSensorCallback {

    private static String computerName;

    static {
        try {
            computerName = java.net.InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) {
            Logger.getLogger(MemoryUsageService.class.getName()).log(Level.SEVERE, null, ex);
            computerName = "undefined";
        }
    }

    private MemoryUsageSensor memoryUsageSenor;

    public MemoryUsageService(URI mqttURI) throws MqttException, UnknownHostException, IOException {
        super(mqttURI, computerName + "MemoryUsageService.s0m3-S3cr3t-1D", new MemoryUsageServiceContract(computerName));
        memoryUsageSenor = new MemoryUsageSensor(this);
        subscribe(getContract().INTENT_MEMORY_USAGE_CALLBACK_PERIOD + "/#", (topic, payload) -> {
            Long callbackPeriod = getMapper().readValue(payload, Long.class);
            memoryUsageSenor.setMemoryUsageCallbackPeriod(callbackPeriod);
            publishStatus(getContract().STATUS_MEMORY_USAGE_CALLBACK_PERIOD, memoryUsageSenor.getMemoryUsageCallbackPeriod());
        });
        connect();

        publishDescription(getContract().EVENT_MEMORY_USAGE, "timestamp: [0.." + Long.MAX_VALUE + "]\n value: \n  total: [0.." + Long.MAX_VALUE + "]\n  free: [0.." + Long.MAX_VALUE + "]");
        publishDescription(getContract().STATUS_MEMORY_USAGE_CALLBACK_PERIOD, "[0.." + Long.MAX_VALUE + "]");
        publishDescription(getContract().INTENT_MEMORY_USAGE_CALLBACK_PERIOD, "[0.." + Long.MAX_VALUE + "]");

    }

    @Override
    public void memoryUsageChanged(PhysicalMemory memory) {
        publishEvent(getContract().EVENT_MEMORY_USAGE, memory);
    }

}
