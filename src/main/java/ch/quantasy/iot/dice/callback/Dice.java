/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.quantasy.iot.dice.callback;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author reto
 */
public class Dice {

    private static final Random RANDOM;

    static {
        RANDOM = new Random();
    }
    private final DiceCallback callback;
    private final List<Object> sides;
    private Object currentSide;

    public Dice(DiceCallback callback) {
        this.callback = callback;
        sides = new ArrayList<>();

    }

    public void setSides(List<Object> sides) {
        if (sides == null) {
            return;
        }
        this.sides.clear();
        this.sides.addAll(sides);
        callback.sidesChanged(getSides());
        play();
    }

    public List<Object> getSides() {
        return new ArrayList<Object>(sides);
    }

    public void play() {
        if (this.sides.size() < 1) {
            return;
        }
        this.currentSide = sides.get(RANDOM.nextInt(this.sides.size()));
        callback.played(currentSide);
    }

    public Object getCurrentSide() {
        return currentSide;
    }

}
