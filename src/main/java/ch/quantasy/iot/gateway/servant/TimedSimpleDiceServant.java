/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.quantasy.iot.gateway.servant;

import ch.quantasy.gateway.service.timer.TimerServiceContract;
import ch.quantasy.iot.gateway.service.cpuLoad.CPULoadService;
import ch.quantasy.iot.gateway.service.dice.simple.SimpleDiceServiceContract;
import ch.quantasy.mqtt.gateway.client.ClientContract;
import ch.quantasy.mqtt.gateway.client.GatewayClient;
import ch.quantasy.mqtt.gateway.client.GCEvent;
import ch.quantasy.timer.DeviceTickerConfiguration;
import java.io.IOException;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.paho.client.mqttv3.MqttException;

/**
 * We need the following addition to the pom.xml
 * <dependency>
 * <groupId>ch.quantasy</groupId>
 * <artifactId>ch.quantasy.timer.mqtt.gateway</artifactId>
 * <version>1.0-SNAPSHOT</version>
 * </dependency>
 *
 * @author reto
 */
public class TimedSimpleDiceServant extends GatewayClient<ClientContract> {

    private static String computerName;

    static {
        try {
            computerName = java.net.InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) {
            Logger.getLogger(CPULoadService.class.getName()).log(Level.SEVERE, null, ex);
            computerName = "undefined";
        }
    }

    SimpleDiceServiceContract simpleDiceServiceContract;
    TimerServiceContract timerServiceContract;

    public TimedSimpleDiceServant(URI mqttURI) throws MqttException {
        super(mqttURI, "ad92f0" + "TimedSimpleDiceServant", new ClientContract("Servant", "WebView", "timedSimpleDiceServant01"));
        connect(); //If connection is made before subscribitions, no 'historical' will be treated of the non-clean session 
        simpleDiceServiceContract = new SimpleDiceServiceContract(computerName);
        timerServiceContract = new TimerServiceContract(computerName);

        subscribe(simpleDiceServiceContract.EVENT_PLAY, (topic, payload) -> {
            GCEvent<Integer>[] events = super.toEventArray(payload, Integer.class);
            System.out.println("Play: " + events[0]);
            publishIntent("Tutorial/WebView/I/text", new PlayState("1", "" + events[0].getValue()));

        });
        subscribe("Tutorial/WebView/+/E/button", (topic, payload) -> {
            publishIntent(simpleDiceServiceContract.INTENT_PLAY, "true");
        });

        subscribe(timerServiceContract.EVENT_TICK + "/playDice", (topic, payload) -> {
            publishIntent(simpleDiceServiceContract.INTENT_PLAY, "true");
        });
        
        publishIntent(timerServiceContract.INTENT_CONFIGURATION, new DeviceTickerConfiguration("playDice",null,0,1000,null));

    }

    static class PlayState {

        private String id;
        private String content;

        private PlayState() {
        }

        public PlayState(String id, String content) {
            this.id = id;
            this.content = content;
        }

        public String getContent() {
            return content;
        }

        public String getId() {
            return id;
        }

    }

    public static void main(String[] args) throws MqttException, InterruptedException, IOException {
        URI mqttURI = URI.create("tcp://127.0.0.1:1883");
        if (args.length > 0) {
            mqttURI = URI.create(args[0]);
        } else {
            System.out.printf("Per default, 'tcp://127.0.0.1:1883' is chosen.\nYou can provide another address as first argument i.e.: tcp://iot.eclipse.org:1883\n");
        }
        System.out.printf("\n%s will be used as broker address.\n", mqttURI);

        TimedSimpleDiceServant r = new TimedSimpleDiceServant(mqttURI);

        System.in.read();
    }

}
