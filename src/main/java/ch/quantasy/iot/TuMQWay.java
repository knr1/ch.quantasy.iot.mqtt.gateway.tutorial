/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.quantasy.iot;

import ch.quantasy.iot.gateway.service.cpuLoad.CPULoadService;
import ch.quantasy.iot.gateway.service.memory.MemoryUsageService;
import java.io.IOException;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author reto
 */
public class TuMQWay {

    private static String computerName;

    static {
        try {
            computerName = java.net.InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) {
            Logger.getLogger(CPULoadService.class.getName()).log(Level.SEVERE, null, ex);
            computerName = "undefined";
        }
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        URI mqttURI = URI.create("tcp://127.0.0.1:1883");
        if (args.length > 0) {
            mqttURI = URI.create(args[0]);
        } else {
            System.out.printf("Per default, '%s' is chosen.\nYou can provide another address as first argument i.e.: tcp://iot.eclipse.org:1883\n",mqttURI.toASCIIString());
        }
        System.out.printf("\n%s will be used as broker address.\n", mqttURI);

        CPULoadService cpuLoadService = new CPULoadService(mqttURI, computerName);
        MemoryUsageService memoryUsageService = new MemoryUsageService(mqttURI, computerName);

        System.in.read();
    }
}
