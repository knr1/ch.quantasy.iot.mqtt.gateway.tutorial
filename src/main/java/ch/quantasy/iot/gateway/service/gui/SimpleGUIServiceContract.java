/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.quantasy.iot.gateway.service.gui;

import ch.quantasy.mqtt.gateway.client.contract.AyamlServiceContract;
import java.util.Map;

/**
 *
 * @author reto
 */
public class SimpleGUIServiceContract extends AyamlServiceContract {

    private final String TEXTFIELD;
    private final String TEXT;
    public final String STATUS_BUTTON_TEXT;
    public final String STATUS_TEXTFIELD_TEXT;
    private final String BUTTON;
    private final String CLICKED;
    public final String EVENT_BUTTON_CLICKED;

    public SimpleGUIServiceContract(String instanceID) {
        super("Tutorial", "SimpleGUI", instanceID);

        TEXTFIELD = "textField";
        TEXT = "text";
        BUTTON = "button";
        CLICKED = "clicked";
        STATUS_TEXTFIELD_TEXT = STATUS + "/" + TEXTFIELD + "/" + TEXT;
        STATUS_BUTTON_TEXT = STATUS + "/" + BUTTON + "/" + TEXT;
        EVENT_BUTTON_CLICKED = EVENT + "/" + BUTTON + "/" + CLICKED;
    }

    @Override
    public void setMessageTopics(Map messageTopicMap) {
        messageTopicMap.put(INTENT, UIIntent.class);
        messageTopicMap.put(EVENT_BUTTON_CLICKED, ButtonClickedEvent.class);
        messageTopicMap.put(STATUS_BUTTON_TEXT, ButtonTextStatus.class);
        messageTopicMap.put(STATUS_TEXTFIELD_TEXT, TextFieldTextStatus.class);
    }

}
