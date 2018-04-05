/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.quantasy.iot.gateway.binding.gui;

import ch.quantasy.mqtt.gateway.client.message.AnIntent;
import ch.quantasy.mqtt.gateway.client.message.annotations.Nullable;
import ch.quantasy.mqtt.gateway.client.message.annotations.StringForm;

/**
 *
 * @author reto
 */
public class UIIntent extends AnIntent{
    @Nullable
    @StringForm
    public String buttonText;
    @Nullable
    @StringForm
    public String textFieldText;      
}
