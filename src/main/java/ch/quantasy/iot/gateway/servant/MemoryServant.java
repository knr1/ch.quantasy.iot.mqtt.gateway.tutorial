/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.quantasy.iot.gateway.servant;

import ch.quantasy.iot.gateway.service.cpuLoad.CPULoadServiceContract;
import ch.quantasy.iot.gateway.service.memory.MemoryUsageServiceContract;
import ch.quantasy.iot.memory.PhysicalMemory;
import ch.quantasy.mqtt.gateway.client.ClientContract;
import ch.quantasy.mqtt.gateway.client.GatewayClient;
import ch.quantasy.mqtt.gateway.client.GCEvent;
import java.io.IOException;
import java.net.URI;
import org.eclipse.paho.client.mqttv3.MqttException;

/**
 *
 * @author reto
 */
public class MemoryServant extends GatewayClient<ClientContract> {

    MemoryUsageServiceContract memServiceContract;
    CPULoadServiceContract cpuServiceContract;

    public MemoryServant(URI mqttURI) throws MqttException {
        super(mqttURI, "ad92f0", new ClientContract("Servant", "Reader", "memory"));
        connect(); //If connection is made before subscribitions, no 'historical' will be treated of the non-clean session 
        memServiceContract = new MemoryUsageServiceContract("prisma");
        subscribe(memServiceContract.EVENT_MEMORY_USAGE, (topic, payload) -> {
            GCEvent<PhysicalMemory>[] events = super.toEventArray(payload, PhysicalMemory.class);
            System.out.println("Memory: " + events[0]);
        });
        cpuServiceContract = new CPULoadServiceContract("prisma");
        subscribe(cpuServiceContract.EVENT_CPU_LOAD, (topic, payload) -> {
            GCEvent<Integer>[] events = super.toEventArray(payload, Integer.class);
            System.out.println("CPU: " + events[0]);
        });
        //connect(); //If connection is made after subscribitions, all 'historical' will be treated of the non-clean session

        //As an example, an Intent to the MemoryService is sent
        publishIntent(memServiceContract.INTENT_MEMORY_USAGE_CALLBACK_PERIOD, 2000);
        //In order to get the CPULoad, send another intent...
        publishIntent(cpuServiceContract.INTENT_CPU_LOAD_CALLBACK_PERIOD, 1000);

    }

    public static void main(String[] args) throws MqttException, InterruptedException, IOException {
        URI mqttURI = URI.create("tcp://127.0.0.1:1883");
        if (args.length > 0) {
            mqttURI = URI.create(args[0]);
        } else {
            System.out.printf("Per default, 'tcp://127.0.0.1:1883' is chosen.\nYou can provide another address as first argument i.e.: tcp://iot.eclipse.org:1883\n");
        }
        System.out.printf("\n%s will be used as broker address.\n", mqttURI);

        MemoryServant r = new MemoryServant(mqttURI);

        System.in.read();
    }

}
