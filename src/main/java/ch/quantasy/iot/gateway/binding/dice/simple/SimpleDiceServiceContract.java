/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.quantasy.iot.gateway.binding.dice.simple;

import ch.quantasy.mqtt.gateway.client.contract.AyamlServiceContract;
import ch.quantasy.mqtt.gateway.client.message.Message;
import java.util.Map;

/**
 *
 * @author reto
 */
public class SimpleDiceServiceContract extends AyamlServiceContract {

    private final String PLAY;
    private final String SIDES;
    public final String STATUS_SIDES;
    public final String EVENT_PLAY;

    public SimpleDiceServiceContract(String instanceID) {
        super("Tutorial", "SimpleDice", instanceID);

        PLAY = "play";
        SIDES = "sides";

        STATUS_SIDES = STATUS + "/" + SIDES;
        EVENT_PLAY = EVENT + "/" + PLAY;
    }

    @Override
    public void setMessageTopics(Map<String, Class<? extends Message>> messageTopicMap) {
        messageTopicMap.put(EVENT_PLAY, PlayEvent.class);
        messageTopicMap.put(STATUS_SIDES, DiceStatus.class);
        messageTopicMap.put(INTENT, DiceIntent.class);
    }
}
