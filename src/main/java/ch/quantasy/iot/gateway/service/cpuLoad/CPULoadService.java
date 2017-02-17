/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.quantasy.iot.gateway.service.cpuLoad;

import ch.quantasy.iot.cpu.CPULoadSensor;
import ch.quantasy.mqtt.gateway.client.GatewayClient;
import java.io.IOException;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.paho.client.mqttv3.MqttException;
import ch.quantasy.iot.cpu.CPULoadSensorCallback;

/**
 *
 * @author reto
 */
public class CPULoadService extends GatewayClient<CPULoadServiceContract> implements CPULoadSensorCallback {

    private static String computerName;

    static {
        try {
            computerName = java.net.InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) {
            Logger.getLogger(CPULoadService.class.getName()).log(Level.SEVERE, null, ex);
            computerName = "undefined";
        }
    }

    private CPULoadSensor cpuLoad;

    public CPULoadService(URI mqttURI) throws MqttException, UnknownHostException, IOException {
        super(mqttURI, computerName + "CPULoadService.s0m3-S3cr3t-1D", new CPULoadServiceContract(computerName));
        cpuLoad = new CPULoadSensor(this);
        subscribe(getContract().INTENT_CPU_LOAD_CALLBACK_PERIOD + "/#", (topic, payload) -> {
            Long callbackPeriod = getMapper().readValue(payload, Long.class);
            cpuLoad.setCPULoadCallbackPeriod(callbackPeriod);
            publishStatus(getContract().STATUS_CPU_LOAD_CALLBACK_PERIOD, cpuLoad.getCpuLoadCallbackPeriod());
        });
        connect();
        publishDescription(getContract().EVENT_CPU_LOAD, "timestamp: [0.." + Long.MAX_VALUE + "]\n value: [0..100]");
        publishDescription(getContract().STATUS_CPU_LOAD_CALLBACK_PERIOD, "[0.." + Long.MAX_VALUE + "]");
        publishDescription(getContract().INTENT_CPU_LOAD_CALLBACK_PERIOD, "[0.." + Long.MAX_VALUE + "]");

    }

    @Override
    public void cpuLoadChanged(int cpuLoad) {
        publishEvent(getContract().EVENT_CPU_LOAD, cpuLoad);
    }
  
}
