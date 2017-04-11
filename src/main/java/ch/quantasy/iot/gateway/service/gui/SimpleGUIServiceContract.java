/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.quantasy.iot.gateway.service.gui;

import ch.quantasy.mqtt.gateway.client.ClientContract;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author reto
 */
public class SimpleGUIServiceContract extends ClientContract {

    private final String TEXTFIELD;
    private final String TEXT;
    public final String INTENT_BUTTON_TEXT;
    public final String STATUS_BUTTON_TEXT;
    private final String BUTTON;
    private final String CLICKED;
    public final String INTENT_TEXTFIELD_TEXT;
    public final String EVENT_BUTTON_CLICKED;

    public SimpleGUIServiceContract(String instanceID) {
        super("Tutorial", "SimpleGUI", instanceID);

        TEXTFIELD = "textField";
        TEXT = "text";
        BUTTON = "button";
        CLICKED = "clicked";

        INTENT_BUTTON_TEXT = INTENT + "/" + BUTTON + "/" + TEXT;
        STATUS_BUTTON_TEXT = STATUS + "/" + BUTTON + "/" + TEXT;
        INTENT_TEXTFIELD_TEXT = INTENT + "/" + TEXTFIELD + "/" + TEXT;
        EVENT_BUTTON_CLICKED = EVENT + "/" + BUTTON + "/" + CLICKED;
    }

    @Override
    protected void describe(Map<String, String> descriptions) {
        descriptions.put(INTENT_BUTTON_TEXT, "<String>");
        descriptions.put(INTENT_TEXTFIELD_TEXT, "<String>");
        descriptions.put(EVENT_BUTTON_CLICKED, "timestamp: [0.." + Long.MAX_VALUE + "]\n value: true");
        descriptions.put(STATUS_BUTTON_TEXT, "<String>");
    }
}
