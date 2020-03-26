

# MQTT-Gateway
ch.quantasy.iot.mqtt.gateway.tutorial

## Tutorial for Simple

To execute and try-out the tutorial, execute the TuMqWay.java and point it to a running MQTT-Broker.
This will start the services and will show a JavaFX-GUI.
In order to test the WebComponent, run a local MQTT-Broker and load the file index.html with Firefox-Based browser.
These browsers accept a connection to localhost, if the file has been loaded from the same machine.
(Chrome would throw a 'not same origin' exception)


This tutorial is example-oriented. The idea is to provide a 'Dice'-choreography starring a 'Dice'-service, a 'GUI'-service and a 'Dice-GUI'-servant.


It is following the idea of the micro-service 'pattern' provided in the [SeMqWay] project. For a full micro-service, the service-source (aka. Dice) is created first and then the service-logic (aka. DiceService) will bind
the source to MQTT (via the convenient GatewayClient).

<a href="https://github.com/knr1/ch.quantasy.iot.mqtt.gateway.tutorial/blob/master/Micro-service-SimpleServant-Full.svg">
<img src="https://github.com/knr1/ch.quantasy.iot.mqtt.gateway.tutorial/blob/master/Micro-service-SimpleServant-Full.svg.png" alt="Choreography of the Micro-services" />
</a>


### Micro-Service source (Model)
<a href="https://github.com/knr1/ch.quantasy.iot.mqtt.gateway.tutorial/blob/master/Micro-service-SimpleDice.svg">
<img src="https://github.com/knr1/ch.quantasy.iot.mqtt.gateway.tutorial/blob/master/Micro-service-SimpleDice.svg.png" alt="Micro-service-Diagram" />
</a>


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
public class SimpleDiceService {

    private final SimpleDice dice;
    private final GatewayClient<SimpleDiceServiceContract> gatewayClient;

    public SimpleDiceService(URI mqttURI, String mqttClientName, String instanceName) throws MqttException {
        gatewayClient=new GatewayClient<>(mqttURI, mqttClientName, new SimpleDiceServiceContract(instanceName));
        dice = new SimpleDice();
        gatewayClient.connect();
        gatewayClient.subscribe(gatewayClient.getContract().INTENT + "/", (topic, payload) -> {
            Set<DiceIntent> intents = gatewayClient.toMessageSet(payload, DiceIntent.class);
            intents.stream().filter((intent) -> (intent.play == true)).map((_item) -> {
                dice.play();
                return _item;
            }).forEachOrdered((_item) -> {
                gatewayClient.readyToPublish(gatewayClient.getContract().EVENT_PLAY, new PlayEvent(dice.getChosenSide()));
            });
        });
        gatewayClient.readyToPublish(gatewayClient.getContract().STATUS_SIDES, new DiceStatus(dice.getAmountOfSides()));

    }

}
```

The missing class is the contract itself. Here it is:

```java
public class SimpleDiceServiceContract extends AyamlServiceContract {

    private final String PLAY;
    private final String SIDES;
    public final String STATUS_SIDES;
    public final String EVENT_PLAY;

    public SimpleDiceServiceContract(String instanceID) {
        super("Tutorial", "SimpleDice", instanceID);

        PLAY = "play";
        SIDES = "sides";

        STATUS_SIDES = STATUS + "/" + SIDES;
        EVENT_PLAY = EVENT + "/" + PLAY;
        addMessageTopic(EVENT_PLAY, PlayEvent.class);
        addMessageTopic(STATUS_SIDES, DiceStatus.class);
        addMessageTopic(INTENT, DiceIntent.class);
    }
}
``` 

As one can see, there are some data structures that are sent back and forth in 
form of a structured document (in our case in YAML notation). Using MQTTGateway as
helper, the developer uses an extension of a Message class (i.e. AnIntent, AnEvent, AStatus), that helps building the document.

```java
public class DiceIntent extends AnIntent{
    public boolean play;
    
    public DiceIntent(boolean play) {
        this.play = play;
    }

    private DiceIntent() {
    }
    
}
```

```java
public class DiceStatus extends AStatus{
    public int sides;

    public DiceStatus(int sides) {
        this.sides = sides;
    }

    private DiceStatus() {
    }
    
}
```

```java
public class PlayEvent extends AnEvent {
    @Range(from = 1, to =6)
    public int chosenSide;

    public PlayEvent(int chosenSide) {
        this.chosenSide = chosenSide;
    }

    private PlayEvent() {
    }
    
}
```

### Start of the Micro-Service
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
### How to access the new Micro-Service
Now, you can choose your favorite programming language / environment (Node-Red works fine as well) and can access the Micro-Service...
Sending a 'play: true' to the intent-topic: 

```
Tutorial/SimpleDice/<computerName>/I
```

... will make the service work.

When the service has done something, it will respond on the event-topic:
```
Tutorial/SimpleDice/<computerName>/E/play
```

Go ahead and subscribe on that topic! (Remember: All messages might be sent within an array.)

If you want to know the status of the micro-service subscribe to the following status-topic:
```
Tutorial/SimpleDice/<computerName>/S/#
```


The following diagram gives the overview of what has just been done:
<a href="https://github.com/knr1/ch.quantasy.iot.mqtt.gateway.tutorial/blob/master/Micro-service-SimpleDice-Full.svg">
<img src="https://github.com/knr1/ch.quantasy.iot.mqtt.gateway.tutorial/blob/master/Micro-service-SimpleDice-Full.svg.png" alt="Micro-service-Diagram" />
</a>

### GUI
Now, we have created a component, ready to serve. We now have to make it accessible. Therefore we create yet another component in any given language.
In a first step a GUI within an OS-based programming language will be presented, then a browser-based GUI will follow

#### OS-GUI
<a href="https://github.com/knr1/ch.quantasy.iot.mqtt.gateway.tutorial/blob/master/SimpleGUI.svg">
<img src="https://github.com/knr1/ch.quantasy.iot.mqtt.gateway.tutorial/blob/master/SimpleGUI.svg.png" alt="Micro-service-Diagram" />
</a>
Any language can be used! Here a simple Java-Client is shown, providing the functionality of the above diagram:

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

### Micro-Service logic (Presenter)
This time, the 'presenter' logic is put into the same class. Again, the convenience class GatewayClient is aggregated as 
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

    public static void main(String... args) {
        launch(args);
    }

    @Override
    public void init() throws Exception {
        Long timeStamp = System.currentTimeMillis();
        URI mqttURI = URI.create("tcp://127.0.0.1:1883");
        //URI mqttURI = URI.create("tcp://iot.eclipse.org:1883");

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
                    MessageCollector collector=new MessageCollector();
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
```

The missing class is the Contract itself. Here it is:

```java
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
        addMessageTopic(INTENT, UIIntent.class);
        addMessageTopic(EVENT_BUTTON_CLICKED, ButtonClickedEvent.class);
        addMessageTopic(STATUS_BUTTON_TEXT, ButtonTextStatus.class);
        addMessageTopic(STATUS_TEXTFIELD_TEXT, TextFieldTextStatus.class);
    }
}
```
Any language can be used! Here a simple Java-Client has been produced. In order to
provide the correct data structures sent and read as yaml-document, the java-implementation
of the MQTTGateway provide the possibility to provide the document as an extension of
Message.java (i.e. AnIntent.class, AnEvent.class, AStatus.class):


```java
public class UIIntent extends AnIntent{
    @Nullable
    @StringForm
    public String buttonText;
    @Nullable
    @StringForm
    public String textFieldText;      
}
```

```java
public class ButtonClickedEvent extends AnEvent {
    public boolean clicked;

    public ButtonClickedEvent(boolean clicked) {
        this.clicked = clicked;
    }

    private ButtonClickedEvent() {
    }  
}
```

```java
public class ButtonTextStatus extends AStatus {
    @StringForm
    public String text;

    public ButtonTextStatus(String text) {
        this.text = text;
    }

    private ButtonTextStatus() {
    }   
}
```



### How to access the new Micro-Service
Now, you can choose your favorite programming language / environment (Node-Red works fine as well) and can access the Micro-Service...
Sending a <string> such as "buttonText: Play" to the intent-topic: 

```
Tutorial/SimpleGUI/<instance>/I
```

... will give the button of the service the name 'Play'.

Sending a <string> such as "textFieldText: helloWorld" to the intent-topic: 

```
Tutorial/SimpleGUI/<instance>/I
```

... will write 'helloWorld' into the textField of the service.

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

### Multiple instances of a Micro-Service
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

### The UI in Java-Script (Browser)
If you want to access the MQTT-broker from Java-Script, You have to access it via WebSocket (As the browsers in 2017 do not accept the MQTT-Protocol via native socket).
However, this does not state any major problem as can be seen in the following.

There exist multiple MQTT-Libraries for JavaScript (Browser and Server). In this case the paho library is used.
First the HTML-File is shown:
```html
<!DOCTYPE html>
<html>
    <head>
        <title>Tutorial</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <script type="text/javascript" src="../js/libs/jquery/jquery-3.1.1.min.js"></script>
        <script type="text/javascript" src="../js/libs/json2yaml/json2yaml.js"></script>
        <script type="text/javascript" src="../js/libs/yaml2json/yaml2json.js"></script>

        <script type="text/javascript" src="../js/libs/mqtt/mqttws31.js"></script>

        <script type="text/javascript" src="./js/Config.js"></script>

        <meta name="viewport" content="width=device-width, initial-scale=1.0">
    </head>
    <body>
        <div id="output"></div>
        <div>Subscribed to <input type='text' id='topic' disabled />
            Status: <input type='text' id='status' size="80" disabled /></div>
        <object id="visual" type="image/svg+xml" data="visual.svg"></object>
        <script type="text/javascript" src="../js/GatewayClient.js"></script>
        <script type="text/javascript" src="./js/Tutorial.js"></script>
    </body>
</html>
</body>
</html>
```

There is no need for jquery or json2yaml and yaml2json. These libraries simply serve for convenience reasons.
The mqttws.js library does the heavy lifting for MQTT over WebSockets.

The remaining files are all tutorial specific.
The GatewayClient.js should serve the same purpose as the GatewayClient.java, however, this is not yet very mature. (Sorry)

```js
var mqtt;
var reconnectTimeout = 2000;
var clientName = "gui" + parseInt((Math.random() * 10000), 10).toString(32);
var instanceTopic=baseTopic+"/"+clientName;

function MQTTconnect() {
    if (typeof path == "undefined") {
        path = '';
    }
    mqtt = new Paho.MQTT.Client(
            host,
            port,
            path,
            "webView_" + parseInt(Math.random() * 1000000, 16)
            );
    var options = {
        timeout: 3,
        useSSL: useTLS,
        cleanSession: cleansession,
        onSuccess: onConnect,
        onFailure: function (message) {
            $('#status').val("Connection failed: " + message.errorMessage + "Retrying");
            setTimeout(MQTTconnect, reconnectTimeout);
        }
    };

    mqtt.onConnectionLost = onConnectionLost;
    mqtt.onMessageArrived = onMessageArrived;

    if (username != null) {
        options.userName = username;
        options.password = password;
    }
    console.log("Host=" + host + ", port=" + port + ", path=" + path + " TLS = " + useTLS + " username=" + username + " password=" + password);
    mqtt.connect(options, {
        will: {
            topic: instanceTopic + "/S/connection",
            payload: 'offline'
        }
    });
    

}


function onConnect() {
    message = new Paho.MQTT.Message("online");
    message.destinationName = instanceTopic + "/S/connection";
    message.retained = true;
    mqtt.send(message);
    $('#status').val('Connected to ' + host + ':' + port + path);
    // Connection succeeded; subscribe to our topic
    intentTopic = instanceTopic + "/I/#";
    mqtt.subscribe(intentTopic, {qos: 1});
    $('#topic').val(intentTopic);
}

function onConnectionLost(response) {
    setTimeout(MQTTconnect, reconnectTimeout);
    $('#status').val("connection lost: " + responseObject.errorMessage + ". Reconnecting");

}
;

function onMessageArrived(message) {
    message.payloadObject = YAML.parse(message.payloadString);
    onIntent(message);
}
;

function testTopic(topic, intent) {
    regex = new RegExp(instanceTopic + "/I" + "(/.*)*" + "/" + intent + "(/.*)*");
    return topic.match(regex);
}


function sendEvent(topic, object) {
    var event = {
        "timestamp": Math.floor((new Date).getTime() / 1000)
    };
    event.value = object;
    var yaml = json2yaml([event]);
    yaml = "---\n" + yaml;
    console.log("to topic: " + instanceTopic + " sending text: " + yaml);
    message = new Paho.MQTT.Message(yaml);
    message.destinationName = instanceTopic + "/E/" + topic;
    message.retained = true;
    mqtt.send(message);
}
function sendStatus(topic, object) {
    var yaml = json2yaml(object);
    yaml = "---\n" + yaml;
    console.log("to topic: " + instanceTopic + " sending text: " + yaml);
    message = new Paho.MQTT.Message(yaml);
    message.destinationName = instanceTopic + "/S/" + topic;
    message.retained = true;
    mqtt.send(message);
}
function sendDescription(topic, object) {
    var yaml = json2yaml(object);
    yaml = "---\n" + yaml;
    console.log("to topic: " + baseTopic + " sending text: " + yaml);
    message = new Paho.MQTT.Message(yaml);
    message.destinationName = baseTopic + "/D/" + topic;
    message.retained = true;
    mqtt.send(message);
}

$(document).ready(function () {
    MQTTconnect();
});
$(window).on("beforeunload", function () {
    message = new Paho.MQTT.Message("offline");
    message.destinationName = instanceTopic + "/S/connection";
    message.retained = true;
    mqtt.send(message);
});
```

The Tutorial.js serves as the  service implementation towards the svg and the GatewayClient
```js
var texts;
var buttons;
var svgDoc;
var visual;

$(window).on("load", function () {
    visual = document.getElementById("visual");
    svgDoc = visual.contentDocument; //get the inner DOM of visual.svg
    //Caution: If working via 'file' and not via 'http' firefox will accept, chrome will throw an exception (cross-origin frame reference) 
    buttons = svgDoc.getElementsByClassName('tutorialButton');
    texts = svgDoc.getElementsByClassName('tutorialText');
    var buttonTexts = svgDoc.getElementsByClassName('tutorialButtonText');
    for (var i = 0; i < buttons.length; i++) {
        var button = buttons[i];
        $(button).click(function (event) {
            var svgElement = button;
            var value = {
                "buttonId": svgElement.getAttribute("buttonId"),
                "action": "clicked"
            }
            sendEvent("button", value);
        });
    }

    document.addEventListener('visibilitychange', onVisibilityChanged);
    //Sorry for the bad 'Descriptions', but the json2yaml converter is not that great. Needs improvement!
    sendDescription("intent/text", {"id": "text", "content": "text"});
    sendDescription("status/visibility", {"state": "visible hidden"});
    sendDescription("event/button", {"timestamp": "[0..]", "value": {"buttonId": "text", "action": "clicked"}});

});

function onVisibilityChanged() {
    var value = {
        "state": document.visibilityState
    }
    sendStatus("visibility", value);
}

function onIntent(message) {
    var topic = message.destinationName;
    var payload = message.payloadObject;

    console.log("Topic=" + topic + ", payload=" + payload);
    if (testTopic(topic, "text")) {
        for (var i = 0; i < texts.length; i++) {
            var text = texts[i];
            var textID = text.getAttribute("textId");
            if (textID === payload.id) {
                content = payload.content;
                text.textContent = content;
            }
        }
    }
}
``` 

# API
But in fact you would not really care how it is implemented... You only care how to access it. Here is the API:



### SimpleDice
```
Tutorial/SimpleDice/U/<id>/E/play
   required: # this tag is not part of the data structure
     chosenSide: Number <from: 1 to: 6>
     timeStamp: Number <from: 0 to: 9223372036854775807> # default: Current time in nano seconds
   
```
```
Tutorial/SimpleDice/U/<id>/I
   required: # this tag is not part of the data structure
     timeStamp: Number <from: 0 to: 9223372036854775807> # default: Current time in nano seconds
   optional: # this tag is not part of the data structure
     amountOfSides: Number <from: 6 to: 6>
     play: Boolean <true,false>
   
```
```
Tutorial/SimpleDice/U/<id>/S/connection
   required: # this tag is not part of the data structure
     timeStamp: Number <from: 0 to: 9223372036854775807> # default: Current time in nano seconds
     value: String <[online, offline]>
   
```
```
Tutorial/SimpleDice/U/<id>/S/sides
   required: # this tag is not part of the data structure
     sides: Number <from: 6 to: 6>
     timeStamp: Number <from: 0 to: 9223372036854775807> # default: Current time in nano seconds
   
```

### SimpleGUI
```
Tutorial/SimpleGUI/U/<id>/E/button/clicked
   required: # this tag is not part of the data structure
     clicked: Boolean <true,false>
     timeStamp: Number <from: 0 to: 9223372036854775807> # default: Current time in nano seconds
   
```
```
Tutorial/SimpleGUI/U/<id>/I
   required: # this tag is not part of the data structure
     timeStamp: Number <from: 0 to: 9223372036854775807> # default: Current time in nano seconds
   optional: # this tag is not part of the data structure
     buttonText: String <regEx: [\S\s]*>
     textFieldText: String <regEx: [\S\s]*>
   
```
```
Tutorial/SimpleGUI/U/<id>/S/button/text
   required: # this tag is not part of the data structure
     text: String <regEx: [\S\s]*>
     timeStamp: Number <from: 0 to: 9223372036854775807> # default: Current time in nano seconds
   
```
```
Tutorial/SimpleGUI/U/<id>/S/connection
   required: # this tag is not part of the data structure
     timeStamp: Number <from: 0 to: 9223372036854775807> # default: Current time in nano seconds
     value: String <[online, offline]>
   
```
```
Tutorial/SimpleGUI/U/<id>/S/textField/text
   required: # this tag is not part of the data structure
     text: String <regEx: [\S\s]*>
     timeStamp: Number <from: 0 to: 9223372036854775807> # default: Current time in nano seconds
   
```


Switch to [Master]

[Master]:<https://github.com/knr1/ch.quantasy.iot.mqtt.gateway.tutorial/tree/master>






 







 [SeMqWay]:<https://github.com/knr1/ch.quantasy.mqtt.gateway>
 [Moquette]:<https://github.com/andsel/moquette>
 [MQTT-Lens]:<https://chrome.google.com/webstore/detail/mqttlens/hemojaaeigabkbcookmlgmdigohjobjm?hl=en>
 [tinkerforge]:<http://www.tinkerforge.com/en>
 [MQTT]: <http://mqtt.org/>
 [TiMqWay.jar]: <https://prof.hti.bfh.ch/knr1/TiMqWay.jar>
 [d3Viewer]: <https://github.com/hardillb/d3-MQTT-Topic-Tree>
 [YAML]: <https://en.wikipedia.org/wiki/YAML>
 [micro-service]: <https://en.wikipedia.org/wiki/Microservices>

