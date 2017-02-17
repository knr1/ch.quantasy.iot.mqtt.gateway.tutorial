/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.quantasy.iot.dice.simple;

import java.util.Random;

/**
 *
 * @author reto
 */
public class SimpleDice {

    private static final Random RANDOM;
    

    static {
        RANDOM = new Random();
    }
    private final int amountOfSides=6;
    private int currentSide;

    public SimpleDice() {
        play();

    }

    public int getAmountOfSides() {
        return amountOfSides;
    }

    public void play() {
        this.currentSide=RANDOM.nextInt(amountOfSides)+1;
    }

    public int getChosenSide() {
        return currentSide;
    }

}
