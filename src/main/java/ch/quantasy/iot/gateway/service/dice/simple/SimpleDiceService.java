/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.quantasy.iot.gateway.service.dice.simple;

import ch.quantasy.iot.dice.simple.SimpleDice;
import ch.quantasy.mqtt.gateway.client.GatewayClient;
import java.net.URI;
import org.eclipse.paho.client.mqttv3.MqttException;

/**
 *
 * @author reto
 */
public class SimpleDiceService extends GatewayClient<SimpleDiceServiceContract> {

    
    private final SimpleDice dice;

    
    public SimpleDiceService(URI mqttURI, String mqttClientName, String instanceName) throws MqttException {
        super(mqttURI, mqttClientName, new SimpleDiceServiceContract(instanceName));
        dice = new SimpleDice();
        connect();
        subscribe(getContract().INTENT_PLAY + "/#", (topic, payload) -> {
            dice.play();
            publishEvent(getContract().EVENT_PLAY, dice.getChosenSide());

        });

        publishStatus(getContract().STATUS_SIDES, dice.getAmountOfSides());

    }

}
