/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.quantasy.iot.gateway.servant;

import ch.quantasy.iot.gateway.binding.servant.SimpleServantContract;
import ch.quantasy.iot.gateway.binding.dice.simple.DiceIntent;
import ch.quantasy.iot.gateway.binding.dice.simple.PlayEvent;
import ch.quantasy.iot.gateway.binding.dice.simple.SimpleDiceServiceContract;
import ch.quantasy.mqtt.gateway.client.ConnectionStatus;
import ch.quantasy.mqtt.gateway.client.GatewayClient;
import java.io.IOException;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
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
            System.out.println("----------");
            SortedSet<PlayEvent> playEvents = super.toMessageSet(payload, PlayEvent.class);
            for (PlayEvent playEvent : playEvents) {
                System.out.println("Play: " + playEvent);
            }
            webViewServiceInstances.forEach((instance) -> {
                System.out.println(instance);
                readyToPublish(instance+"/I", new PlayStateIntent("1", "" + playEvents.last().chosenSide));
            });

        });
        subscribe("Tutorial/WebView/+/E/button", (topic, payload) -> {
                 readyToPublish(simpleDiceServiceContract.INTENT, new DiceIntent(true));
        });

        subscribe("Tutorial/WebView/+/S/connection", (topic, payload) -> {
            ConnectionStatus status = new TreeSet<>(toMessageSet(payload, ConnectionStatus.class)).last();
            String webViewServiceInstance = topic.replaceFirst("/S/connection", "");
            System.out.println(webViewServiceInstance + " " + status);

            if (status.value.equals("online")) {
                webViewServiceInstances.add(webViewServiceInstance);
            } else {
                webViewServiceInstances.remove(webViewServiceInstance);
            }
        });

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
