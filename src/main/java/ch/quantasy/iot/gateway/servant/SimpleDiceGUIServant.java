/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.quantasy.iot.gateway.servant;

import ch.quantasy.iot.gateway.service.dice.simple.DiceIntent;
import ch.quantasy.iot.gateway.service.dice.simple.PlayEvent;
import ch.quantasy.iot.gateway.service.dice.simple.SimpleDiceServiceContract;
import ch.quantasy.iot.gateway.service.gui.SimpleGUIServiceContract;
import ch.quantasy.iot.gateway.service.gui.UIIntent;
import ch.quantasy.mqtt.gateway.client.GatewayClient;
import ch.quantasy.mqtt.gateway.client.message.MessageCollector;
import ch.quantasy.mqtt.gateway.client.message.PublishingMessageCollector;
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
    private Set<SimpleGUIServiceContract> simpleGUIServiceInstances;

    private final MessageCollector collector;
    private PublishingMessageCollector<SimpleServantContract> publishingCollector;

    public SimpleDiceGUIServant(URI mqttURI, String instanceName) throws MqttException {
        super(mqttURI, "SimpleDiceGUIServant" + instanceName, new SimpleServantContract("Tutorial/Servant", "SimpleDiceGUI", instanceName));
        collector = new MessageCollector();
        publishingCollector = new PublishingMessageCollector(collector, this);
        simpleGUIServiceInstances = new HashSet<>();
        connect(); //If connection is made before subscribitions, no 'historical' will be treated of the non-clean session 
        simpleDiceServiceContract = new SimpleDiceServiceContract(instanceName);

        subscribe("Tutorial/SimpleGUI/U/+/S/connection", (topic, payload) -> {
            String status = super.getMapper().readValue(payload, String.class);
            String simpleGUIServiceInstance = topic.replaceFirst("Tutorial/SimpleGUI/U/", "").replaceFirst("/S/connection", "");
            System.out.println(simpleGUIServiceInstance + " " + status);
            SimpleGUIServiceContract simpleGUIServiceContract = new SimpleGUIServiceContract(simpleGUIServiceInstance);

            if (status.equals("online")) {
                UIIntent uiIntent = new UIIntent();
                uiIntent.buttonText = "play";
                publishingCollector.readyToPublish(simpleGUIServiceContract.INTENT, uiIntent);
                simpleGUIServiceInstances.add(simpleGUIServiceContract);
                super.subscribe(simpleGUIServiceContract.EVENT_BUTTON_CLICKED, (eventTopic, eventPayload) -> {
                    publishingCollector.readyToPublish(simpleDiceServiceContract.INTENT, new DiceIntent(true));
                });
            } else {
                simpleGUIServiceInstances.remove(simpleGUIServiceInstance);
            }
        });

        subscribe(simpleDiceServiceContract.EVENT_PLAY, (topic, payload) -> {
            Set<PlayEvent> playEvents = super.toMessageSet(payload, PlayEvent.class);
            for (PlayEvent playEvent : playEvents) {
                simpleGUIServiceInstances.forEach((instance) -> {
                    UIIntent uiIntent = new UIIntent();
                    uiIntent.textFieldText = "" + playEvent.chosenSide;
                    publishingCollector.readyToPublish(instance.INTENT, uiIntent);
                });
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

        SimpleDiceGUIServant r = new SimpleDiceGUIServant(mqttURI, computerName);

        System.in.read();
    }

}
