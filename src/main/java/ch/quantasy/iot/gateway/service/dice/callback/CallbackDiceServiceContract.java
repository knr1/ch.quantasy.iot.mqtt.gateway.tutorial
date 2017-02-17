/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.quantasy.iot.gateway.service.dice.callback;

import ch.quantasy.mqtt.gateway.client.ClientContract;

/**
 *
 * @author reto
 */
public class CallbackDiceServiceContract extends ClientContract {

    private final String PLAY;
    private final String SIDES;
    public final String INTENT_PLAY;
    public final String INTENT_SIDES;
    public final String STATUS_SIDES;
    public final String EVENT_PLAY;

    public CallbackDiceServiceContract(String instanceID) {
        super("Tutorial", "CallbackDice", instanceID);

        PLAY = "play";
        SIDES = "sides";

        INTENT_PLAY = INTENT + "/" + PLAY;
        INTENT_SIDES = INTENT + "/" + SIDES;
        STATUS_SIDES = STATUS + "/" + SIDES;
        EVENT_PLAY = EVENT + "/" + PLAY;
    }

}
