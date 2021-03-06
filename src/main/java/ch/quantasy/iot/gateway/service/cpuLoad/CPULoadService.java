/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.quantasy.iot.gateway.service.cpuLoad;

import ch.quantasy.iot.cpu.CPULoadSensor;
import java.io.IOException;
import java.net.URI;
import java.net.UnknownHostException;
import ch.quantasy.iot.cpu.CPULoadSensorCallback;
import ch.quantasy.iot.gateway.service.cpuLoad.message.CPULoadEvent;
import ch.quantasy.iot.gateway.service.cpuLoad.message.CPULoadIntent;
import ch.quantasy.iot.gateway.service.cpuLoad.message.CPULoadStatus;
import ch.quantasy.mdsmqtt.gateway.client.MQTTGatewayClient;

/**
 *
 * @author reto
 */
public class CPULoadService extends MQTTGatewayClient<CPULoadServiceContract> implements CPULoadSensorCallback {

    private CPULoadSensor cpuLoadSensor;

    public CPULoadService(URI mqttURI,String computerName) throws UnknownHostException, IOException {
        super(mqttURI, computerName + "CPULoadService.s0m3-S3cr3t-1D", new CPULoadServiceContract(computerName),false);
        cpuLoadSensor = new CPULoadSensor(this);
        subscribe(getContract().INTENT + "/#", (topic, payload) -> {
            CPULoadIntent intent = toMessageSet(payload, CPULoadIntent.class).last();
            if (intent.isValid()) {
                cpuLoadSensor.setCPULoadCallbackPeriod(intent.callbackPeriod);
                readyToPublish(getContract().STATUS_CPU_LOAD_CALLBACK_PERIOD, new CPULoadStatus(cpuLoadSensor.getCpuLoadCallbackPeriod()));
            }
        });
        connect();

    }

    @Override
    public void cpuLoadChanged(int cpuLoad) {
        readyToPublish(getContract().EVENT_CPU_LOAD, new CPULoadEvent(cpuLoad));
    }

}
