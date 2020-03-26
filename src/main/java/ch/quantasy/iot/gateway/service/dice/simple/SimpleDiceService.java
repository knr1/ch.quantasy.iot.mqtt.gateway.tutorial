/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.quantasy.iot.gateway.service.dice.simple;

import ch.quantasy.iot.gateway.binding.dice.simple.DiceIntent;
import ch.quantasy.iot.gateway.binding.dice.simple.SimpleDiceServiceContract;
import ch.quantasy.iot.gateway.binding.dice.simple.PlayEvent;
import ch.quantasy.iot.gateway.binding.dice.simple.DiceStatus;
import ch.quantasy.iot.dice.simple.SimpleDice;
import ch.quantasy.mdsmqtt.gateway.client.MQTTGatewayClient;
import java.net.URI;
import java.util.SortedSet;

/**
 *
 * @author reto
 */
public class SimpleDiceService {

    private final SimpleDice dice;
    private final MQTTGatewayClient<SimpleDiceServiceContract> gatewayClient;

    public SimpleDiceService(URI mqttURI, String mqttClientName, String instanceName){
        gatewayClient = new MQTTGatewayClient<>(mqttURI, mqttClientName, new SimpleDiceServiceContract(instanceName),true);
        dice = new SimpleDice();
        gatewayClient.connect();
        gatewayClient.subscribe(gatewayClient.getContract().INTENT + "/#", (topic, payload) -> {
            SortedSet<DiceIntent> intents = gatewayClient.toMessageSet(payload, DiceIntent.class);
            intents.forEach(intent -> {
                if (intent.play) {
                    dice.play();
                    gatewayClient.readyToPublish(gatewayClient.getContract().EVENT_PLAY, new PlayEvent(dice.getChosenSide()));
                }
            });
        });
        gatewayClient.readyToPublish(gatewayClient.getContract().STATUS_SIDES, new DiceStatus(dice.getAmountOfSides()));
    }

}
