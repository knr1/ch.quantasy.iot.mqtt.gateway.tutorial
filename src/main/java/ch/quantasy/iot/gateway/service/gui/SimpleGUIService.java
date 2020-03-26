/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.quantasy.iot.gateway.service.gui;

import ch.quantasy.iot.gateway.binding.gui.TextFieldTextStatus;
import ch.quantasy.iot.gateway.binding.gui.UIIntent;
import ch.quantasy.iot.gateway.binding.gui.ButtonClickedEvent;
import ch.quantasy.iot.gateway.binding.gui.SimpleGUIServiceContract;
import ch.quantasy.iot.gateway.binding.gui.ButtonTextStatus;
import ch.quantasy.mdservice.message.MessageCollector;
import ch.quantasy.mdsmqtt.gateway.client.MQTTGatewayClient;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URI;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 *
 * @author reto
 */
public class SimpleGUIService {

    private final String instanceName;

    

    private final MQTTGatewayClient<SimpleGUIServiceContract> gatewayClient;

    public SimpleGUIService(URI mqttURI,String computerName){
        Long timeStamp = System.currentTimeMillis();
     
        String mqttClientName = "SimpleGUI" + computerName + ":" + timeStamp;
        instanceName = (timeStamp % 10000) + "@" + computerName;
        
            gatewayClient = new MQTTGatewayClient(mqttURI, mqttClientName, new SimpleGUIServiceContract(instanceName),true);
            gatewayClient.connect();
            init();
    }

    public void init() {
        JFrame frame=new JFrame("Simple GUI: "+instanceName);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                gatewayClient.disconnect();
            }
            
        });
     
        frame.setSize(600, 200);
        JButton button = new JButton();
        button.setText("...");
        JTextField textField = new JTextField();
        textField.setEditable(false);
        gatewayClient.readyToPublish(gatewayClient.getContract().STATUS_BUTTON_TEXT, new ButtonTextStatus(button.getText()));
        gatewayClient.readyToPublish(gatewayClient.getContract().STATUS_TEXTFIELD_TEXT, new TextFieldTextStatus(textField.getText()));

        button.addActionListener((arg0) -> {
                    gatewayClient.readyToPublish(gatewayClient.getContract().EVENT_BUTTON_CLICKED, new ButtonClickedEvent(true));
});
       
        gatewayClient.subscribe(gatewayClient.getContract().INTENT + "/#", (String topic, byte[] payload) -> {
            SwingUtilities.invokeLater(() -> {
                try {
                    MessageCollector collector = new MessageCollector();
                    Set<UIIntent> uiIntents = gatewayClient.toMessageSet(payload, UIIntent.class);
                    collector.add(topic, uiIntents);
                    for (UIIntent intent : uiIntents) {
                        if (intent.buttonText != null) {
                            button.setText(intent.buttonText);
                            gatewayClient.readyToPublish(gatewayClient.getContract().STATUS_BUTTON_TEXT, new ButtonTextStatus(button.getText()));
                        }
                        if (intent.textFieldText != null) {
                            textField.setText(intent.textFieldText);
                            gatewayClient.readyToPublish(gatewayClient.getContract().STATUS_TEXTFIELD_TEXT, new TextFieldTextStatus(textField.getText()));
                        }
                    }
                } catch (Exception ex) {
                    Logger.getLogger(SimpleGUIService.class.getName()).log(Level.SEVERE, null, ex);
                }

            });
        });

        JPanel gridPanel = new JPanel(new GridLayout(2,0));
        gridPanel.add(textField);
        gridPanel.add(button);
        frame.getContentPane().add(gridPanel);
        frame.setVisible(true);
    }
}
