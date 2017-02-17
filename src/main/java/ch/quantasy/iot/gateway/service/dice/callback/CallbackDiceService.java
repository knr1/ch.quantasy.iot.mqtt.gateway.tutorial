/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.quantasy.iot.gateway.service.dice.callback;

import ch.quantasy.iot.dice.callback.Dice;
import ch.quantasy.iot.dice.callback.DiceCallback;
import ch.quantasy.iot.gateway.service.cpuLoad.CPULoadService;
import ch.quantasy.mqtt.gateway.client.GatewayClient;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.paho.client.mqttv3.MqttException;

/**
 *
 * @author reto
 */
public class CallbackDiceService extends GatewayClient<CallbackDiceServiceContract> implements DiceCallback {
    private static String computerName;
    private final Dice dice;
    static {
        try {
            computerName = java.net.InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) {
            Logger.getLogger(CPULoadService.class.getName()).log(Level.SEVERE, null, ex);
            computerName = "undefined";
        }
    }
    public CallbackDiceService(URI mqttURI) throws MqttException {
        super(mqttURI, "wef9ph324hav4"+computerName, new CallbackDiceServiceContract(computerName));
        dice=new Dice(this);
        connect();
        subscribe(getContract().INTENT_PLAY+"/#", (topic, payload) -> {
            Boolean play=getMapper().readValue(payload, Boolean.class);
            if(play){
                dice.play();
            }
        });
        subscribe(getContract().INTENT_SIDES+"/#", (topic, payload) -> {
            List<Object> sides=getMapper().readValue(payload, List.class);
            dice.setSides(sides);
        });
        
        publishDescription(getContract().EVENT_PLAY, "timestamp: [0.." + Long.MAX_VALUE + "]\n value: any");
        publishDescription(getContract().STATUS_SIDES, "[any..any]");
        publishDescription(getContract().INTENT_PLAY, "true");
        publishDescription(getContract().INTENT_SIDES, "[any..any]");

    }

    @Override
    public void sidesChanged(List<Object> sides) {
        publishStatus(getContract().STATUS_SIDES, sides);
    }

    @Override
    public void played(Object o) {
        publishEvent(getContract().EVENT_PLAY, o);
    }
    
    
}
