/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.quantasy.iot.gateway.servant;

import ch.quantasy.iot.gateway.service.dice.simple.SimpleDiceServiceContract;
import ch.quantasy.mqtt.gateway.client.AClientContract;
import ch.quantasy.mqtt.gateway.client.GCEvent;
import ch.quantasy.mqtt.gateway.client.GatewayClient;
import java.io.IOException;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.paho.client.mqttv3.MqttException;

/**
 *
 * @author reto
 */
public class SimpleDiceGUIServant extends GatewayClient<SimpleServantContract> {

    private SimpleDiceServiceContract simpleDiceServiceContract;
    private Set<String> simpleGUIServiceInstances;

    public SimpleDiceGUIServant(URI mqttURI, String instanceName) throws MqttException {
        super(mqttURI, "SimpleDiceGUIServant" + instanceName, new SimpleServantContract("Tutorial/Servant", "SimpleDiceGUI", instanceName));
        simpleGUIServiceInstances = new HashSet<>();
        connect(); //If connection is made before subscribitions, no 'historical' will be treated of the non-clean session 
        simpleDiceServiceContract = new SimpleDiceServiceContract(instanceName);

        subscribe("Tutorial/SimpleGUI/+/S/connection", (topic, payload) -> {
            String status = super.getMapper().readValue(payload, String.class);
            String simpleGUIServiceInstance = topic.replaceFirst("/S/connection", "");
            System.out.println(simpleGUIServiceInstance + " " + status);

            if (status.equals("online")) {
                super.publishIntent(simpleGUIServiceInstance + "/I/button/text", "play");
                simpleGUIServiceInstances.add(simpleGUIServiceInstance);
                super.subscribe(simpleGUIServiceInstance + "/E/button/clicked", (eventTopic, eventPayload) -> {
                    super.publishIntent(simpleDiceServiceContract.INTENT_PLAY, true);
                });
            } else {
                simpleGUIServiceInstances.remove(simpleGUIServiceInstance);
            }
        });

        subscribe(simpleDiceServiceContract.EVENT_PLAY, (topic, payload) -> {
            GCEvent<Integer>[] events = super.toEventArray(payload, Integer.class);
            if (events == null || events.length == 0) {
                return;
            }
            simpleGUIServiceInstances.forEach((instance) -> {
                super.publishIntent(instance + "/I/textField/text", events[0].getValue());
            });

        });
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

    private static String computerName;

    static {
        try {
            computerName = java.net.InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) {
            Logger.getLogger(SimpleDiceGUIServant.class.getName()).log(Level.SEVERE, null, ex);
            computerName = "undefined";
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

        SimpleDiceGUIServant r = new SimpleDiceGUIServant(mqttURI, computerName);

        System.in.read();
    }

}
