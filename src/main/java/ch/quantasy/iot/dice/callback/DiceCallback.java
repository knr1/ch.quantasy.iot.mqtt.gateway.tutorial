/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.quantasy.iot.dice.callback;

import java.util.List;

/**
 *
 * @author reto
 */
public interface DiceCallback {
    public void sidesChanged(List<Object> sides);
    public void played(Object o);
}
