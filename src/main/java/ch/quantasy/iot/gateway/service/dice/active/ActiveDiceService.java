/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.quantasy.iot.gateway.service.dice.active;

import ch.quantasy.iot.dice.active.ActiveDice;
import ch.quantasy.iot.dice.active.ActiveDiceCallback;
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
public class ActiveDiceService extends GatewayClient<ActiveDiceServiceContract> implements ActiveDiceCallback {

    private static String computerName;
    private final ActiveDice dice;

    static {
        try {
            computerName = java.net.InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) {
            Logger.getLogger(CPULoadService.class.getName()).log(Level.SEVERE, null, ex);
            computerName = "undefined";
        }
    }

    public ActiveDiceService(URI mqttURI) throws MqttException {
        super(mqttURI, "er98h0023hf" + computerName, new ActiveDiceServiceContract(computerName));
        dice = new ActiveDice(this);
        connect();
        subscribe(getContract().INTENT_PLAY_STATE + "/#", (topic, payload) -> {
            Boolean playState = getMapper().readValue(payload, Boolean.class);
            dice.setPlayState(playState);
        });
        subscribe(getContract().INTENT_PLAY_CALLBACK_PERIOD + "/#", (topic, payload) -> {
            Long period = getMapper().readValue(payload, Long.class);
            dice.setDicePlayPeriod(period);
        });
        subscribe(getContract().INTENT_PLAY_CALLBACK_PERIOD + "/#", (topic, payload) -> {
            Long callbackPeriod = getMapper().readValue(payload, Long.class);
            dice.setDicePlayPeriod(callbackPeriod);
            publishStatus(getContract().STATUS_PLAY_CALLBACK_PERIOD, dice.getDicePlayPeriod());
        });

        publishDescription(getContract().EVENT_PLAY, "timestamp: [0.." + Long.MAX_VALUE + "]\n value: [1.." + Integer.MAX_VALUE + "]");
        publishDescription(getContract().STATUS_PLAY_STATE, "[true|false]");
        publishDescription(getContract().INTENT_PLAY_STATE, "[true|false]");
        publishDescription(getContract().INTENT_PLAY_CALLBACK_PERIOD, "[0.." + Long.MAX_VALUE + "]");

    }

    @Override
    public void played(int i) {
        publishEvent(getContract().EVENT_PLAY, i);
    }

    @Override
    public void playStateChanged(boolean playState) {
        publishStatus(getContract().STATUS_PLAY_STATE, playState);
    }

}
