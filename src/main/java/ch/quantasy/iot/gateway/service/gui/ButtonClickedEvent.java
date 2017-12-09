/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.quantasy.iot.gateway.service.gui;

import ch.quantasy.iot.gateway.service.dice.simple.*;
import ch.quantasy.mqtt.gateway.client.message.AnEvent;
import ch.quantasy.mqtt.gateway.client.message.annotations.Range;

/**
 *
 * @author reto
 */
public class ButtonClickedEvent extends AnEvent {
    boolean clicked;

    public ButtonClickedEvent(boolean clicked) {
        this.clicked = clicked;
    }

    private ButtonClickedEvent() {
    }

    
}
