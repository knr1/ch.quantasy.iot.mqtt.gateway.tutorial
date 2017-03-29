/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.quantasy.iot;

import ch.quantasy.gateway.TimerMqWay;
import static ch.quantasy.gateway.TimerMqWay.computerName;
import ch.quantasy.gateway.service.timer.TimerService;
import ch.quantasy.iot.gateway.servant.SimpleDiceGUIServant;
import ch.quantasy.iot.gateway.servant.SimpleDiceWebViewServant;
import ch.quantasy.iot.gateway.servant.timerDice.TimerDiceServant;
import ch.quantasy.iot.gateway.service.dice.simple.SimpleDiceService;
import ch.quantasy.iot.gateway.service.gui.SimpleGUIService;
import java.io.IOException;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.paho.client.mqttv3.MqttException;

/**
 * This is a convenience class that starts the Services and the Servant. Please
 * note, that these Classes could be started within their own processes even on
 * different computers. The only important thing is, that they connect to the
 * same MQTT-broker instance.
 *
 * @author reto
 */
public class TuMQWay {

    private static String computerName;

    static {
        try {
            computerName = java.net.InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) {
            Logger.getLogger(TuMQWay.class.getName()).log(Level.SEVERE, null, ex);
            computerName = "undefined";
        }
    }

    public static void main(String[] args) throws MqttException, InterruptedException, IOException {
        URI mqttURI = URI.create("tcp://127.0.0.1:1883");
        if (args.length > 0) {
            mqttURI = URI.create(args[0]);
        } else {
            System.out.printf("Per default, 'tcp://127.0.0.1:1883' is chosen.\nYou can provide another address as first argument i.e.: tcp://iot.eclipse.org:1883\n");
        }
        System.out.printf("\n%s will be used as broker address.\n", mqttURI);

        SimpleDiceService simpleDeviceService = new SimpleDiceService(mqttURI, "SimpleDice" + computerName, computerName);
        SimpleDiceGUIServant simpleDiceGUIServant = new SimpleDiceGUIServant(mqttURI);
        SimpleDiceWebViewServant simpleDiceWebViewServant = new SimpleDiceWebViewServant(mqttURI);

        TimerService s = new TimerService(mqttURI, computerName);

        
        TimerDiceServant timerDiceServant=new TimerDiceServant(mqttURI);
        
        SimpleGUIService.main(mqttURI.toString());

        System.in.read();
    }
}
