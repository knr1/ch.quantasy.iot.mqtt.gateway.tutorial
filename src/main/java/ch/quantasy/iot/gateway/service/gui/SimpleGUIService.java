/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.quantasy.iot.gateway.service.gui;

import ch.quantasy.mqtt.gateway.client.GatewayClient;
import java.io.IOException;
import java.net.URI;
import java.net.UnknownHostException;
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

    public SimpleGUIService() {
        Long timeStamp = System.currentTimeMillis();
        URI mqttURI = URI.create("tcp://127.0.0.1:1883");
        String mqttClientName = "SimpleGUI" + computerName + ":" + timeStamp;
        instanceName = (timeStamp % 10000) + "@" + computerName;
        try {
            gatewayClient = new GatewayClient<>(mqttURI, mqttClientName, new SimpleGUIServiceContract(instanceName));
            gatewayClient.connect();
            gatewayClient.publishDescription(gatewayClient.getContract().INTENT_BUTTON_TEXT, "<String>");
            gatewayClient.publishDescription(gatewayClient.getContract().INTENT_TEXTFIELD_TEXT, "<String>");
            gatewayClient.publishDescription(gatewayClient.getContract().EVENT_BUTTON_CLICKED, "timestamp: [0.." + Long.MAX_VALUE + "]\n value: true");
            gatewayClient.publishDescription(gatewayClient.getContract().STATUS_BUTTON_TEXT, "<String>");
        } catch (MqttException ex) {
            Logger.getLogger(SimpleGUIService.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void main(String... args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Simple GUI: " + instanceName);
        stage.setWidth(600);

        Button button = new Button();
        button.setText("...");
        gatewayClient.publishStatus(gatewayClient.getContract().STATUS_BUTTON_TEXT, "...");
        
        button.setOnAction((ActionEvent event) -> {
            gatewayClient.publishEvent(gatewayClient.getContract().EVENT_BUTTON_CLICKED, "true");
        });
        gatewayClient.subscribe(gatewayClient.getContract().INTENT_BUTTON_TEXT+ "/#", (String topic, byte[] payload) -> {
            Platform.runLater(() -> {
                try {
                    button.setText(gatewayClient.getMapper().readValue(payload, String.class));
                } catch (IOException ex) {
                    Logger.getLogger(SimpleGUIService.class.getName()).log(Level.SEVERE, null, ex);
                }
                gatewayClient.publishStatus(gatewayClient.getContract().STATUS_BUTTON_TEXT, button.getText());

            });
        });

        TextField textField = new TextField();
        textField.setEditable(false);
        gatewayClient.subscribe(gatewayClient.getContract().INTENT_TEXTFIELD_TEXT+ "/#", (String topic, byte[] payload) -> {
            Platform.runLater(() -> {
                try {
                    textField.setText(gatewayClient.getMapper().readValue(payload, String.class));
                } catch (IOException ex) {
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
