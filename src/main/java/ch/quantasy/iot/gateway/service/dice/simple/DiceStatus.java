/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.quantasy.iot.gateway.service.dice.simple;

import ch.quantasy.mqtt.gateway.client.message.AStatus;

/**
 *
 * @author reto
 */
public class DiceStatus extends AStatus{
    public int sides;

    public DiceStatus(int sides) {
        this.sides = sides;
    }

    private DiceStatus() {
    }
    
}
