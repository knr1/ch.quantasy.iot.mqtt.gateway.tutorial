/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.quantasy.iot.gateway.service.gui;

import ch.quantasy.mqtt.gateway.client.message.AStatus;
import ch.quantasy.mqtt.gateway.client.message.annotations.StringForm;

/**
 *
 * @author reto
 */
public class ButtonTextStatus extends AStatus {
    @StringForm
    public String text;

    public ButtonTextStatus(String text) {
        this.text = text;
    }

    private ButtonTextStatus() {
    }   
}
