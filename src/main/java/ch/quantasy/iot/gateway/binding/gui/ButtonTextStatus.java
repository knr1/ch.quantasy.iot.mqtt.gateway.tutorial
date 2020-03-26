/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.quantasy.iot.gateway.binding.gui;

import ch.quantasy.mdservice.message.AStatus;
import ch.quantasy.mdservice.message.annotations.StringForm;

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
