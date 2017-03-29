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

    private static String computerName;
    public static final String DISCRIMINATOR = "simpleDice";

    static {
        try {
            computerName = java.net.InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) {
            Logger.getLogger(SimpleDiceGUIServant.class.getName()).log(Level.SEVERE, null, ex);
            computerName = "undefined";
        }
    }

    private SimpleDiceServiceContract simpleDiceServiceContract;
    private TimerServiceContract timerServiceContract;

    public TimerDiceServant(URI mqttURI) throws MqttException {
        super(mqttURI, "pu34083" + "TimerDiceServant"+computerName, new TimerDiceServantContract("Tutorial/Servant", "TimerDice", computerName));
        publishDescription(getContract().INTENT_CONFIGURATION, "first: [null|0.." + Long.MAX_VALUE + "]\n interval: [null|1.." + Long.MAX_VALUE + "]\n last: [null|0.." + Long.MAX_VALUE + "]\n");
        publishDescription(getContract().STATUS_CONFIGURATION, "first: [null|0.." + Long.MAX_VALUE + "]\n interval: [null|1.." + Long.MAX_VALUE + "]\n last: [null|0.." + Long.MAX_VALUE + "]\n");
      
        simpleDiceServiceContract = new SimpleDiceServiceContract(computerName);
        timerServiceContract = new TimerServiceContract(computerName);
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

}
