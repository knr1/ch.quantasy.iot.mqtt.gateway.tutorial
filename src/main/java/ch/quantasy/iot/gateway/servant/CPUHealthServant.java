/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.quantasy.iot.gateway.servant;

import ch.quantasy.iot.gateway.service.cpuLoad.CPULoadService;
import ch.quantasy.iot.gateway.service.cpuLoad.CPULoadServiceContract;
import ch.quantasy.iot.gateway.service.cpuLoad.message.CPULoadEvent;
import ch.quantasy.iot.gateway.service.cpuLoad.message.CPULoadIntent;
import ch.quantasy.iot.gateway.service.memory.MemoryUsageServiceContract;
import ch.quantasy.iot.gateway.service.memory.message.MemoryUsageEvent;
import ch.quantasy.iot.gateway.service.memory.message.MemoryUsageIntent;
import ch.quantasy.mqtt.gateway.client.GatewayClient;
import ch.quantasy.mqtt.gateway.client.contract.AyamlServiceContract;
import java.io.IOException;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.SortedSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.paho.client.mqttv3.MqttException;

/**
 *
 * @author reto
 */
public class CPUHealthServant extends GatewayClient<AyamlServiceContract> {

    public static String computerName;
    static {
        try {
            computerName = java.net.InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) {
            Logger.getLogger(CPULoadService.class.getName()).log(Level.SEVERE, null, ex);
            computerName = "undefined";
        }
    }
    MemoryUsageServiceContract memServiceContract;
    CPULoadServiceContract cpuServiceContract;

    public CPUHealthServant(URI mqttURI) throws MqttException {
        super(mqttURI, "ad92f0", new CPUHealthServantContract("Servant", "Reader", "cpuHealth"));
        connect(); //If connection is made before subscribitions, no 'historical' will be treated of the non-clean session 
        memServiceContract = new MemoryUsageServiceContract("prisma");
        subscribe(memServiceContract.EVENT_MEMORY_USAGE, (topic, payload) -> {
            SortedSet<MemoryUsageEvent> events=toMessageSet(payload, MemoryUsageEvent.class);
            System.out.println(topic+": " + Arrays.toString(events.toArray()));
        });
        cpuServiceContract = new CPULoadServiceContract("prisma");
        subscribe(cpuServiceContract.EVENT_CPU_LOAD, (topic, payload) -> {
            SortedSet<CPULoadEvent> events = toMessageSet(payload, CPULoadEvent.class);
            System.out.println(topic+": " + Arrays.toString(events.toArray()));
        });
        //connect(); //If connection is made after subscribitions, all 'historical' will be treated of the non-clean session

        //As an example, an Intent to the MemoryService is sent
        readyToPublish(memServiceContract.INTENT, new MemoryUsageIntent(2000L));
        //In order to get the CPULoad, send another intent...
        readyToPublish(cpuServiceContract.INTENT, new CPULoadIntent(1000L));

    }

    public static void main(String[] args) throws MqttException, InterruptedException, IOException {
        URI mqttURI = URI.create("tcp://127.0.0.1:1883");
        if (args.length > 0) {
            mqttURI = URI.create(args[0]);
        } else {
            System.out.printf("Per default, 'tcp://127.0.0.1:1883' is chosen.\nYou can provide another address as first argument i.e.: tcp://iot.eclipse.org:1883\n");
        }
        System.out.printf("\n%s will be used as broker address.\n", mqttURI);

        CPUHealthServant r = new CPUHealthServant(mqttURI);

        System.in.read();
    }

}
