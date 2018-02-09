/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.quantasy.iot.gateway.servant;

import ch.quantasy.mqtt.gateway.client.message.AnIntent;

/**
 *
 * @author reto
 */
public class PlayStateIntent extends AnIntent {

    public String id;
    public String content;

    private PlayStateIntent() {
    }

    public PlayStateIntent(String id, String content) {
        this.id = id;
        this.content = content;
    }

}
