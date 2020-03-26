/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.quantasy.iot.gateway.binding.dice.simple;

import ch.quantasy.mdservice.message.AStatus;
import ch.quantasy.mdservice.message.annotations.Range;

/**
 *
 * @author reto
 */
public class DiceStatus extends AStatus{
    @Range(from = 6, to = 6)
    public int sides;

    public DiceStatus(int sides) {
        this.sides = sides;
    }

    private DiceStatus() {
    }
    
}
