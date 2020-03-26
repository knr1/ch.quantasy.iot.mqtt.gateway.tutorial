/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.quantasy.iot.gateway.binding.dice.simple;

import ch.quantasy.mdservice.message.AnIntent;
import ch.quantasy.mdservice.message.annotations.Nullable;
import ch.quantasy.mdservice.message.annotations.Range;

/**
 *
 * @author reto
 */
public class DiceIntent extends AnIntent{
    @Nullable
    public Boolean play;
    @Nullable
    @Range(from = 6,to = 6)
    public Integer amountOfSides;
    
    public DiceIntent(boolean play) {
        this.play = play;
    }

    private DiceIntent() {
    }
    
}
