/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.quantasy.iot.gateway.binding.gui;

import ch.quantasy.mqtt.gateway.client.message.AnEvent;

/**
 *
 * @author reto
 */
public class ButtonClickedEvent extends AnEvent {
    public boolean clicked;

    public ButtonClickedEvent(boolean clicked) {
        this.clicked = clicked;
    }

    private ButtonClickedEvent() {
    }  
}
