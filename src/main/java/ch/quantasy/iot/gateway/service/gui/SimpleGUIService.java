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
import ch.quantasy.mqtt.gateway.client.GatewayClient;
import ch.quantasy.mqtt.gateway.client.message.MessageCollector;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.eclipse.paho.client.mqttv3.MqttException;

/**
 *
 * @author reto
 */
public class SimpleGUIService extends Application {

    private static String computerName;
    private String instanceName;

    static {
        try {
            computerName = java.net.InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) {
            Logger.getLogger(SimpleGUIService.class.getName()).log(Level.SEVERE, null, ex);
            computerName = "undefined";
        }
    }

    private GatewayClient<SimpleGUIServiceContract> gatewayClient;

    public static void main(String... args) {
        launch(args);
    }

    @Override
    public void init() throws Exception {
        Long timeStamp = System.currentTimeMillis();
        //URI mqttURI = URI.create("tcp://147.87.116.3:1883");
        URI mqttURI = URI.create("tcp://127.0.0.1:1883");
        //URI mqttURI = URI.create("tcp://iot.eclipse.org:1883");
        //URI mqttURI=URI.create("ssl://iot.eclipse.org:8883");

        String mqttClientName = "SimpleGUI" + computerName + ":" + timeStamp;
        instanceName = (timeStamp % 10000) + "@" + computerName;
        try {
            gatewayClient = new GatewayClient<>(mqttURI, mqttClientName, new SimpleGUIServiceContract(instanceName));
            gatewayClient.connect();

        } catch (MqttException ex) {
            Logger.getLogger(SimpleGUIService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Simple GUI: " + instanceName);
        stage.setWidth(600);

        Button button = new Button();
        button.setText("...");
        TextField textField = new TextField();
        textField.setEditable(false);
        gatewayClient.readyToPublish(gatewayClient.getContract().STATUS_BUTTON_TEXT, new ButtonTextStatus(button.getText()));
        gatewayClient.readyToPublish(gatewayClient.getContract().STATUS_TEXTFIELD_TEXT, new TextFieldTextStatus(textField.getText()));

        button.setOnAction((ActionEvent event) -> {
            gatewayClient.readyToPublish(gatewayClient.getContract().EVENT_BUTTON_CLICKED, new ButtonClickedEvent(true));
        });
        gatewayClient.subscribe(gatewayClient.getContract().INTENT + "/#", (String topic, byte[] payload) -> {
            Platform.runLater(() -> {
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

        VBox vbox = new VBox();
        vbox.setAlignment(Pos.TOP_CENTER);

        vbox.getChildren().addAll(textField, button);
        stage.setScene(new Scene(vbox));
        stage.show();

    }
}
