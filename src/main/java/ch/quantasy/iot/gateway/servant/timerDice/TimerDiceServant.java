/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.quantasy.iot.gateway.servant.timerDice;

import ch.quantasy.gateway.service.timer.TimerServiceContract;
import ch.quantasy.iot.gateway.servant.SimpleDiceGUIServant;
import ch.quantasy.iot.gateway.service.dice.simple.SimpleDiceServiceContract;
import ch.quantasy.mqtt.gateway.client.GatewayClient;
import ch.quantasy.timer.DeviceTickerConfiguration;
import java.io.IOException;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.paho.client.mqttv3.MqttException;

/**
 *
 * @author reto
 */
public class TimerDiceServant extends GatewayClient<TimerDiceServantContract> {

    
    public static final String DISCRIMINATOR = "simpleDice";

    private SimpleDiceServiceContract simpleDiceServiceContract;
    private TimerServiceContract timerServiceContract;

    public TimerDiceServant(URI mqttURI,String instanceName) throws MqttException {
        super(mqttURI, "pu34083" + "TimerDiceServant"+instanceName, new TimerDiceServantContract("Tutorial/Servant", "TimerDice", instanceName));
        publishDescription(getContract().INTENT_CONFIGURATION, "first: [null|0.." + Long.MAX_VALUE + "]\n interval: [null|1.." + Long.MAX_VALUE + "]\n last: [null|0.." + Long.MAX_VALUE + "]\n");
        publishDescription(getContract().STATUS_CONFIGURATION, "first: [null|0.." + Long.MAX_VALUE + "]\n interval: [null|1.." + Long.MAX_VALUE + "]\n last: [null|0.." + Long.MAX_VALUE + "]\n");
      
        simpleDiceServiceContract = new SimpleDiceServiceContract(instanceName);
        timerServiceContract = new TimerServiceContract(instanceName);
        subscribe(timerServiceContract.EVENT_TICK + "/" + DISCRIMINATOR, (topic, payload) -> {
            publishIntent(simpleDiceServiceContract.INTENT_PLAY, true);
        });

        subscribe(getContract().INTENT_CONFIGURATION + "/#", (topic, payload) -> {
            try {
                TimerDiceConfiguration configuration = super.getMapper().readValue(payload, TimerDiceConfiguration.class);
                DeviceTickerConfiguration timerConfig = new DeviceTickerConfiguration(DISCRIMINATOR, configuration.getEpoch(), configuration.getFirst(), configuration.getInterval(), configuration.getLast());
                publishIntent(timerServiceContract.INTENT_CONFIGURATION, timerConfig);
            } catch (Exception ex) {
                Logger.getLogger(TimerDiceServant.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        subscribe(timerServiceContract.STATUS_CONFIGURATION + "/" + DISCRIMINATOR, (topic, payload) -> {
            TimerDiceConfiguration configuration=this.getMapper().readValue(payload, TimerDiceConfiguration.class);
                publishStatus(getContract().STATUS_CONFIGURATION, configuration);
        });
        connect(); //If connection is made before subscribitions, no 'historical' will be treated of the non-clean

    }
    
    private static String computerName;

    static {
        try {
            computerName = java.net.InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) {
            Logger.getLogger(SimpleDiceGUIServant.class.getName()).log(Level.SEVERE, null, ex);
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

        TimerDiceServant r = new TimerDiceServant(mqttURI,computerName);

        System.in.read();
    }

}
