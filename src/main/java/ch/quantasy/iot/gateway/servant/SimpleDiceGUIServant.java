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
import ch.quantasy.iot.gateway.binding.gui.SimpleGUIServiceContract;
import ch.quantasy.iot.gateway.binding.gui.UIIntent;
import ch.quantasy.mqtt.gateway.client.ConnectionStatus;
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
    private SimpleGUIServiceContract anySimpleGUIServiceContract;
    private Set<SimpleGUIServiceContract> simpleGUIServiceInstances;

    public SimpleDiceGUIServant(URI mqttURI, String instanceName) throws MqttException {
        super(mqttURI, "SimpleDiceGUIServant" + instanceName, new SimpleServantContract("Tutorial/Servant", "SimpleDiceGUI", instanceName));
        simpleGUIServiceInstances = new HashSet<>();
        connect(); //If connection is made before subscribitions, no 'historical' will be treated of the non-clean session 
        simpleDiceServiceContract = new SimpleDiceServiceContract(instanceName);
        anySimpleGUIServiceContract=new SimpleGUIServiceContract("+");

        subscribe(anySimpleGUIServiceContract.STATUS_CONNECTION, (topic, payload) -> {
            System.out.println("Payload: "+new String(payload));
            ConnectionStatus status = toMessageSet(payload, ConnectionStatus.class).last();
            String simpleGUIServiceInstance = topic.replaceFirst("Tutorial/SimpleGUI/U/", "").replaceFirst("/S/connection", "");
            SimpleGUIServiceContract simpleGUIServiceContract = new SimpleGUIServiceContract(simpleGUIServiceInstance);

            if (status.value.equals("online")) {
                UIIntent uiIntent = new UIIntent();
                uiIntent.buttonText = "play";
                readyToPublish(simpleGUIServiceContract.INTENT, uiIntent);
                simpleGUIServiceInstances.add(simpleGUIServiceContract);
                super.subscribe(simpleGUIServiceContract.EVENT_BUTTON_CLICKED, (eventTopic, eventPayload) -> {
                    readyToPublish(simpleDiceServiceContract.INTENT, new DiceIntent(true));
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
                    readyToPublish(instance.INTENT, uiIntent);
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
