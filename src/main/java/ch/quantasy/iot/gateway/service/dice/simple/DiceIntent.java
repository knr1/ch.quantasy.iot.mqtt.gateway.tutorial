/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.quantasy.iot.gateway.service.dice.simple;

import ch.quantasy.mqtt.gateway.client.message.AnIntent;

/**
 *
 * @author reto
 */
public class DiceIntent extends AnIntent{
    public boolean play;

    public DiceIntent(boolean play) {
        this.play = play;
    }

    private DiceIntent() {
    }
    
}
