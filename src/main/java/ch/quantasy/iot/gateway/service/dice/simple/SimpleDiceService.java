/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.quantasy.iot.gateway.service.dice.simple;

import ch.quantasy.iot.dice.simple.SimpleDice;
import ch.quantasy.iot.gateway.service.cpuLoad.CPULoadService;
import ch.quantasy.mqtt.gateway.client.GatewayClient;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.paho.client.mqttv3.MqttException;

/**
 *
 * @author reto
 */
public class SimpleDiceService extends GatewayClient<SimpleDiceServiceContract> {

    private static String computerName;
    private final SimpleDice dice;

    static {
        try {
            computerName = java.net.InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) {
            Logger.getLogger(CPULoadService.class.getName()).log(Level.SEVERE, null, ex);
            computerName = "undefined";
        }
    }

    public SimpleDiceService(URI mqttURI, String mqttClientName, String instanceName) throws MqttException {
        super(mqttURI, mqttClientName, new SimpleDiceServiceContract(instanceName));
        dice = new SimpleDice();
        connect();
        subscribe(getContract().INTENT_PLAY + "/#", (topic, payload) -> {
            dice.play();
            publishEvent(getContract().EVENT_PLAY, dice.getChosenSide());

        });

        publishDescription(getContract().EVENT_PLAY, "timestamp: [0.." + Long.MAX_VALUE + "]\n value: [1.." + Integer.MAX_VALUE + "]");
        publishDescription(getContract().STATUS_SIDES, "[1.." + Integer.MAX_VALUE + "]");
        publishDescription(getContract().INTENT_PLAY, "true");

        publishStatus(getContract().STATUS_SIDES, dice.getAmountOfSides());

    }

}
