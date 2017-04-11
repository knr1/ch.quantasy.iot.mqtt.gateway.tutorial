/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.quantasy.iot.gateway.servant.timerDice;

import ch.quantasy.mqtt.gateway.client.ClientContract;
import java.util.Map;

/**
 *
 * @author reto
 */
public class SimpleServantContract extends ClientContract {

    public SimpleServantContract(String rootContext, String baseClass, String instance) {
        super(rootContext, baseClass, instance);
    }

    @Override
    protected void describe(Map<String, String> descriptions) {
//no furhter descriptions
    }

}
