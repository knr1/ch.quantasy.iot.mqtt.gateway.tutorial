/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.quantasy.iot.gateway.service.dice.simple;

import ch.quantasy.mqtt.gateway.client.ClientContract;
import java.util.Map;

/**
 *
 * @author reto
 */
public class SimpleDiceServiceContract extends ClientContract {

    private final String PLAY;
    private final String SIDES;
    public final String INTENT_PLAY;
    public final String STATUS_SIDES;
    public final String EVENT_PLAY;

    public SimpleDiceServiceContract(String instanceID) {
        super("Tutorial", "SimpleDice", instanceID);

        PLAY = "play";
        SIDES = "sides";

        INTENT_PLAY = INTENT + "/" + PLAY;
        STATUS_SIDES = STATUS + "/" + SIDES;
        EVENT_PLAY = EVENT + "/" + PLAY;
    }

    @Override
    protected void describe(Map<String, String> descriptions) {
        descriptions.put(EVENT_PLAY, "timestamp: [0.." + Long.MAX_VALUE + "]\n value: [1.." + Integer.MAX_VALUE + "]");
        descriptions.put(STATUS_SIDES, "[1.." + Integer.MAX_VALUE + "]");
        descriptions.put(INTENT_PLAY, "true");
    }

}
