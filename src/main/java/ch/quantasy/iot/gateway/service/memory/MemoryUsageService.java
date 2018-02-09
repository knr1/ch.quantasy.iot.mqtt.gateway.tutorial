/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.quantasy.iot.gateway.service.memory;

import ch.quantasy.iot.gateway.service.memory.message.MemoryUsageEvent;
import ch.quantasy.iot.gateway.service.memory.message.MemoryUsageIntent;
import ch.quantasy.iot.gateway.service.memory.message.MemoryUsageStatus;
import ch.quantasy.iot.memory.MemoryUsageSensor;
import ch.quantasy.iot.memory.MemoryUsageSensorCallback;
import ch.quantasy.iot.memory.PhysicalMemory;
import ch.quantasy.mqtt.gateway.client.GatewayClient;
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

    

    private MemoryUsageSensor memoryUsageSenor;

    public MemoryUsageService(URI mqttURI,String computerName) throws MqttException, UnknownHostException, IOException {
        super(mqttURI, computerName + "MemoryUsageService.s0m3-S3cr3t-1D", new MemoryUsageServiceContract(computerName));
        memoryUsageSenor = new MemoryUsageSensor(this);
        subscribe(getContract().INTENT + "/#", (topic, payload) -> {
            MemoryUsageIntent intent = toMessageSet(payload, MemoryUsageIntent.class).last();
            if (intent.isValid()) {
                memoryUsageSenor.setMemoryUsageCallbackPeriod(intent.memoryUsageCallbackPeriod);
                readyToPublish(getContract().STATUS_MEMORY_USAGE_CALLBACK_PERIOD, new MemoryUsageStatus(memoryUsageSenor.getMemoryUsageCallbackPeriod()));
            }
        });
        connect();
    }

    @Override
    public void memoryUsageChanged(PhysicalMemory memory) {
        readyToPublish(getContract().EVENT_MEMORY_USAGE, new MemoryUsageEvent(memory.getTotal(), memory.getFree()));
    }

}
