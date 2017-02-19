

# MQTT-Gateway
ch.quantasy.iot.mqtt.gateway.tutorial

This tutorial introduces one possible way of how to program within the world of event-driven micro-services.

## Difference to generic Micro-Services
The ideology of micro-service]s is simple: Do one thing no more, no less... Provide a simple API.
However, one micro-service alone does not do a lot... so many of them need to be ready and orchestrated in order to provide the power of achieving the desired holistic system
-- the choreography.
This is all sound and every one agrees. However, when it comes to the question of how to orchestrate the services, there are many different solutions with their pros and cons:
If the services are allowed to talk to each other, the ideology can be reduced to 'everything is a service'. Hence no hierarchical structure is expressed explicitely, the choreography
is reached by 'letting' the services orchestrate themselves.

But, as one might guess, this anarchic way of compiling services in this very loose manner, results in an exponential complexity for each new service that is brought to the system.
It starts very easy and one service requires the need of another... It seems the perfect solution for every problem. As soon as the first circular dependency occurs things
start to worsen. And when finally the choreography does not match the desired holistic system anymore, reasoning dawns the person in process of debugging, that this
anarchic 'mess' cannot be controlled. It is like a typical 'goto' program, which is very fast programmed, but unmaintainable after all.

Within this tutorial, a structured micro-service API is presented, which is introduced in the [SeMqWay] project. There, the idea of the structured service is provided and
a GatewayClient providing some convenience-methods in Java. However, this idea works in any programming language and the GatewayClient is no must.

##Tutorial

This tutorial is example-oriented. The idea is to provide a 'Dice'-Service.

It is following the idea of the micro-service 'pattern' provided in the [SeMqWay] project. For a full micro-service, the service-source (aka. Dice) is created first and then the service-logic (aka. DiceService) will bind
the source to MQTT (via the convenient GatewayClient).

<a href="https://github.com/knr1/ch.quantasy.iot.mqtt.gateway.tutorial/blob/master/Micro-service-SimpleDice.svg">
<img src="https://github.com/knr1/ch.quantasy.iot.mqtt.gateway.tutorial/blob/master/Micro-service-SimpleDice.svg.png" alt="Micro-service-Diagram" />
</a>

###Micro-Service source (Model)

Here, the tutorial starts by providing a POJ-program (Dice) which will then be made accessible as a micro-service.

```java
public class SimpleDice {
    private static final Random random;

    static {
        random = new Random();
    }
    private final int amountOfSides=6;
    private int currentSide;

    public SimpleDice() {
        play();
    }

    public int getAmountOfSides() {
        return amountOfSides;
    }

    public void play() {
        this.currentSide=random.nextInt(amountOfSides)+1;
    }

    public int getChosenSide() {
        return currentSide;
    }
}
```

###Micro-Service logic (Presenter)
In a second step, a second program provides the micro-service abilities:

```java
public class SimpleDiceService extends GatewayClient<SimpleDiceServiceContract> {

    private final SimpleDice dice;

    public SimpleDiceService(URI mqttURI, String mqttClientName, String serviceInstanceName) throws MqttException {
        super(mqttURI, mqttClientName, new SimpleDiceServiceContract(serviceInstanceName));
        dice = new SimpleDice();
        connect();
        subscribe(getContract().INTENT_PLAY + "/#", (topic, payload) -> {
            dice.play();
            publishEvent(getContract().EVENT_PLAY, dice.getChosenSide());

        });

        publishDescription(getContract().EVENT_PLAY, "timestamp: [0.." + Long.MAX_VALUE + "]\n value: [1.." + Integer.MAX_VALUE + "]");
        publishDescription(getContract().STATUS_SIDES, "[1.." + Integer.MAX_VALUE + "]");
        publishDescription(getContract().INTENT_PLAY, "true");

        publishStatus(getContract().STATUS_SIDES, dice.getAmountOfSides());

    }
```

The missing class is the Contract itself. Here it is:

```java
public class SimpleDiceServiceContract extends ClientContract {

    private final String PLAY;
    private final String SIDES;
    public final String INTENT_PLAY;
    public final String INTENT_SIDES;
    public final String STATUS_SIDES;
    public final String EVENT_PLAY;

    public SimpleDiceServiceContract(String instanceID) {
        super("Tutorial", "SimpleDice", instanceID);

        PLAY = "play";
        SIDES = "sides";

        INTENT_PLAY = INTENT + "/" + PLAY;
        INTENT_SIDES = INTENT + "/" + SIDES;
        STATUS_SIDES = STATUS + "/" + SIDES;
        EVENT_PLAY = EVENT + "/" + PLAY;
    }

}
``` 

###Start of the Micro-Service
Now what?!
Now there is a fully fledged micro-service ready to serve.

Start it on any machine as follows, you might change the mqtt-address...:

```java
public class TuMQWay {
     private static String computerName;

    static {
        try {
            computerName = java.net.InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) {
            Logger.getLogger(CPULoadService.class.getName()).log(Level.SEVERE, null, ex);
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
        SimpleDiceService simpleDeviceService=new SimpleDiceService(mqttURI,"SimpleDice"+computerName,computerName);
      
        System.in.read();
    }
}

```
###How to access the new Micro-Service
Now, you can choose your favorite programming language / environment (Node-Red works fine as well) and can access the Micro-Service...
Sending a 'true' to the intent-topic: 

```
Tutorial/SimpleDice/<computerName>/I/play
```

... will make the service work.

When the service has done something, it will respond on the event-topic:
```
Tutorial/SimpleDice/<computerName>/E/play
```

Go ahead and subscribe on that topic! (Remember: All events are always sent within an array.)

If you want to know the status of the micro-service subscribe to the following status-topic:
```
Tutorial/SimpleDice/<computerName>/S/#
```

You will always receive the latest status the micro-service is operating in.


The following diagram gives the overview of what has just been done:
<a href="https://github.com/knr1/ch.quantasy.iot.mqtt.gateway.tutorial/blob/master/Micro-service-SimpleDice-Full.svg">
<img src="https://github.com/knr1/ch.quantasy.iot.mqtt.gateway.tutorial/blob/master/Micro-service-SimpleDice-Full.svg.png" alt="Micro-service-Diagram" />
</a>

###GUI
Now, we have created a component, ready to serve. We now have to make it accessible. Therefore we create yet another component in any given language.
In a first step a GUI within an OS-based programming language will be presented, then a browser-based GUI will follow

####OS-GUI
Any language can be used! Here a simple Java-Client is shown, which looks like this:
<a href="https://github.com/knr1/ch.quantasy.iot.mqtt.gateway.tutorial/blob/master/SimpleGUI.svg">
<img src="https://github.com/knr1/ch.quantasy.iot.mqtt.gateway.tutorial/blob/master/SimpleGUI.svg.png" alt="Micro-service-Diagram" />
</a>
And has the following code:

```java
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

    public SimpleGUIService() {
        Long timeStamp = System.currentTimeMillis();
        instanceName = (timeStamp % 10000) + "@" + computerName;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Simple GUI: " + instanceName);
        stage.setWidth(600);

        Button button = new Button();
        button.setText("...");
       
        TextField textField = new TextField();
        textField.setEditable(false);
       
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.TOP_CENTER);

        vbox.getChildren().addAll(textField, button);
        stage.setScene(new Scene(vbox));
        stage.show();

    }
}

```

###Micro-Service logic (Presenter)
This time, the 'presenter' logic is put into the same class. As this class
already extends 'Application', the convenience class GatewayClient is aggregated as 
a uses relation-ship. Here is the complete class for the SimpleGUI:
```java
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

    public static void main(String[] args) {
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
        gatewayClient.subscribe(gatewayClient.getContract().INTENT_BUTTON_TEXT, (String topic, byte[] payload) -> {
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
        gatewayClient.subscribe(gatewayClient.getContract().INTENT_TEXTFIELD_TEXT, (String topic, byte[] payload) -> {
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
```

The missing class is the Contract itself. Here it is:

```java
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
}
```
###How to access the new Micro-Service
Now, you can choose your favorite programming language / environment (Node-Red works fine as well) and can access the Micro-Service...
Sending a <string> such as "Play" to the intent-topic: 

```
Tutorial/SimpleGUI/<instance>/I/button/text
```

... will give the button of the service a name.

Sending a <string> such as "helloWorld" to the intent-topic: 

```
Tutorial/SimpleGUI/<instance>/I/textField/text
```

... will write into the textField of the service.

When the button of the service has been clicked, it will respond on the event-topic:
```
Tutorial/SimpleGUI/<instance>/E/button/clicked
```

Go ahead and subscribe on that topic! (Remember: All events are always sent within an array.)

If you want to know the status of the micro-service subscribe to the following status-topic:
```
Tutorial/SimpleGUI/<instance>/S/#
```

You will always receive the latest status the micro-service is operating in.

###Multiple instances of a Micro-Service
As you might have realized, the instance name is not only the computer name, but some arbitrary number. This allows the co-existance of
multiple instances of the same service on the same 'computer'.

Hence, in order to get the button clicked events of all instances of this micro-service, one would subscribe to the following topic:
```
Tutorial/SimpleGUI/+/E/button/clicked

``` 

The following diagram gives the overview of what has just been done:
<a href="https://github.com/knr1/ch.quantasy.iot.mqtt.gateway.tutorial/blob/master/Micro-service-SimpleGUI-Full.svg">
<img src="https://github.com/knr1/ch.quantasy.iot.mqtt.gateway.tutorial/blob/master/Micro-service-SimpleGUI-Full.svg.png" alt="Micro-service-Diagram" />
</a>




## Servant: Glueing Micro-Services
As micro-services are completely agnostic to their surrounding (they feel as they would be completely alone within the system), there is
something needed in order to glue the micro-services together. This is equivalent to the MVP pattern, where neither the model M nor the view V know each other.
The 'glueing' is done via the presenter P. In the language of event-driven micro-services this is called an orchestrator. Here it is called a servant. It all is the
same, but it these names should explain the context in which the program runs:
Services are controlled and managed by Servants (orchestration). Servants are controlled and managed by (an) Agent(s) (Choreographer).








 







 [SeMqWay]:<https://github.com/knr1/ch.quantasy.mqtt.gateway>
 [Moquette]:<https://github.com/andsel/moquette>
 [MQTT-Lens]:<https://chrome.google.com/webstore/detail/mqttlens/hemojaaeigabkbcookmlgmdigohjobjm?hl=en>
 [tinkerforge]:<http://www.tinkerforge.com/en>
 [MQTT]: <http://mqtt.org/>
 [TiMqWay.jar]: <https://prof.hti.bfh.ch/knr1/TiMqWay.jar>
 [d3Viewer]: <https://github.com/hardillb/d3-MQTT-Topic-Tree>
 [YAML]: <https://en.wikipedia.org/wiki/YAML>
 [micro-service]: <https://en.wikipedia.org/wiki/Microservices>

