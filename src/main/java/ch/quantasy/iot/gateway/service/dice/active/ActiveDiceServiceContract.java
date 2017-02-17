/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.quantasy.iot.gateway.service.dice.active;

import ch.quantasy.iot.gateway.service.dice.callback.*;
import ch.quantasy.mqtt.gateway.client.ClientContract;

/**
 *
 * @author reto
 */
public class ActiveDiceServiceContract extends ClientContract {

    private final String PLAY;
    private final String STATE;
    public final String INTENT_PLAY_STATE;
    public final String STATUS_PLAY_STATE;
    public final String EVENT_PLAY;
    private final String CALLBACK_PERIOD;
    public final String INTENT_PLAY_CALLBACK_PERIOD;
    public final String STATUS_PLAY_CALLBACK_PERIOD;

    public ActiveDiceServiceContract(String instanceID) {
        super("Tutorial", "ActiveDice", instanceID);

        PLAY = "play";
        STATE = "state";
        CALLBACK_PERIOD = "callbackPeriod";

        INTENT_PLAY_STATE = INTENT + "/" + PLAY + "/" + STATE;
        EVENT_PLAY = EVENT + "/" + PLAY;
        STATUS_PLAY_STATE = STATUS + "/" + PLAY + "/" + STATE;
        INTENT_PLAY_CALLBACK_PERIOD=INTENT+"/"+PLAY+"/"+CALLBACK_PERIOD;
        STATUS_PLAY_CALLBACK_PERIOD=STATUS+"/"+PLAY+"/"+CALLBACK_PERIOD;
    }

}
