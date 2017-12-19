/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.quantasy.iot.gateway.servant;

import ch.quantasy.iot.gateway.service.dice.simple.PlayEvent;
import ch.quantasy.iot.gateway.service.dice.simple.SimpleDiceServiceContract;
import ch.quantasy.mqtt.gateway.client.ConnectionStatus;
import ch.quantasy.mqtt.gateway.client.GatewayClient;
import java.io.IOException;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.paho.client.mqttv3.MqttException;

/**
 *
 * @author reto
 */
public class SimpleDiceWebViewServant extends GatewayClient<SimpleServantContract> {

    SimpleDiceServiceContract simpleDiceServiceContract;
    private Set<String> webViewServiceInstances;

    public SimpleDiceWebViewServant(URI mqttURI, String instanceName) throws MqttException {
        super(mqttURI, "ad92f0" + "SimpleDiceServant" + instanceName, new SimpleServantContract("Tutorial/Servant", "WebView", instanceName));
        webViewServiceInstances = new HashSet<>();
        connect(); //If connection is made before subscribitions, no 'historical' will be treated of the non-clean session 
        simpleDiceServiceContract = new SimpleDiceServiceContract(instanceName);
        subscribe(simpleDiceServiceContract.EVENT_PLAY, (topic, payload) -> {
            Set<PlayEvent> playEvents = super.toMessageSet(payload, PlayEvent.class);
            for (PlayEvent playEvent : playEvents) {
                System.out.println("Play: " + playEvent);
            }
            webViewServiceInstances.forEach((instance) -> {
                System.out.println(instance);
          //      super.publishIntent(instance + "/I", new PlayState("1", "" + events[0].getValue()));
            });

        });
        subscribe("Tutorial/WebView/+/E/button", (topic, payload) -> {
       //     publishIntent(simpleDiceServiceContract.INTENT_PLAY, "true");
        });

        subscribe("Tutorial/WebView/+/S/connection", (topic, payload) -> {
            ConnectionStatus status = new TreeSet<>(toMessageSet(payload, ConnectionStatus.class)).last();
            String simpleGUIServiceInstance = topic.replaceFirst("/S/connection", "");
            System.out.println(simpleGUIServiceInstance + " " + status);

            if (status.equals("online")) {
                webViewServiceInstances.add(simpleGUIServiceInstance);
            } else {
                webViewServiceInstances.remove(simpleGUIServiceInstance);
            }
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

        SimpleDiceWebViewServant r = new SimpleDiceWebViewServant(mqttURI, computerName);

        System.in.read();
    }

}
