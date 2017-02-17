/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.quantasy.iot.dice.active;

/**
 *
 * @author reto
 */
public interface ActiveDiceCallback {
    public void played(int chosenSide);
    public void playStateChanged(boolean playState);
}
