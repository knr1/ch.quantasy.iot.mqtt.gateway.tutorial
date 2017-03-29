/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.quantasy.iot.gateway.servant.timerDice;

import ch.quantasy.mqtt.gateway.client.ClientContract;

/**
 *
 * @author reto
 */
public class TimerDiceServantContract extends ClientContract{

    private final String CONFIGURATION;
    public final String INTENT_CONFIGURATION;
    public final String STATUS_CONFIGURATION;
    
    public TimerDiceServantContract(String rootContext, String baseClass) {
        this(rootContext, baseClass,null);
    }

    public TimerDiceServantContract(String rootContext, String baseClass, String instance) {
        super(rootContext, baseClass, instance);
        CONFIGURATION="configuration";
        INTENT_CONFIGURATION=INTENT+"/"+CONFIGURATION;
        STATUS_CONFIGURATION=STATUS+"/"+CONFIGURATION;
    }
    
   
    
}
