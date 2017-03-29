

# MQTT-Gateway
ch.quantasy.iot.mqtt.gateway.tutorial

## Tutorial for Timed

To execute and try-out the tutorial, execute the TuMqWay.java and point it to a running MQTT-Broker.
This will start the services and will show a JavaFX-GUI.
In order to test the WebComponent, run a local MQTT-Broker and load the file index.html with Firefox-Based browser.
These browsers accept a connection to localhost, if the file has been loaded from the same machine.
(Chrome would throw a 'not same origin' exception)


This tutorial is example-oriented. The idea is to provide a 'Timed Dice'-choreography starring a 'Dice'-service, a 'Timer'-service, a 'DiceTimer'-servant, 'GUI'-service a 'Dice-GUI'-service and a 'Dice-GUI'-servant.


It is following the idea of the micro-service 'pattern' provided in the [SeMqWay] project. For a full micro-service, the service-source (aka. Dice) is created first and then the service-logic (aka. DiceService) will bind
the source to MQTT (via the convenient GatewayClient).

<a href="https://github.com/knr1/ch.quantasy.iot.mqtt.gateway.tutorial/blob/master/Micro-service-TimedServant-Full.svg">
<img src="https://github.com/knr1/ch.quantasy.iot.mqtt.gateway.tutorial/blob/master/Micro-service-TimedServant-Full.svg.png" alt="Choreography of the Micro-services" />
</a>


### Micro-Service source (Model)

Here, the tutorial starts by using the Dice-service that has been provided by the previous tutorial-step.
Furthermore, the tutorial expects an instance of a Timer-Service, as is provided by: [TimerMqWay]

The only thing left to do is to provide a link between the Dice-Service and the Timer-Service.

```java
public class TimerDiceServant extends GatewayClient<TimerDiceServantContract> {

    public static final String DISCRIMINATOR = "simpleDice";

    private SimpleDiceServiceContract simpleDiceServiceContract;
    private TimerServiceContract timerServiceContract;

    public TimerDiceServant(URI mqttURI,String mqttClientName, String serviceInstanceName) throws MqttException {
        super(mqttURI, "pu34083" + "TimerDiceServant"+serviceInstanceName, new TimerDiceServantContract("Tutorial/Servant", "TimerDice", serviceInstanceName));
      
        simpleDiceServiceContract = new SimpleDiceServiceContract(serviceInstanceName);
        timerServiceContract = new TimerServiceContract(serviceInstanceName);
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
```

What can be seen here is, that there is a special 'Configuration' object which is unique to the servant. It is a slightly smaller
version of the original 'TimerConfiguration' object provided by the timer service. It is a simple POJO which can be (de-)serialized).

```java
public class TimerDiceConfiguration {

    private Long epoch;
    private Integer first;
    private Integer interval;
    private Integer last;

    /**
     *
     * @param id Identifier of the ticker to be configured
     * @param epoch {@link #setEpoch(java.lang.Long) }
     * @param first {@link #setFirst(java.lang.Long) }
     * @param interval {@link #setInterval(java.lang.Long) }
     * @param last {@link #setLast(java.lang.Long) }
     */
    public TimerDiceConfiguration(Long epoch, Integer first, Integer interval, Integer last) {
        this.epoch = epoch;
        this.first = first;
        this.interval = interval;
        this.last = last;
    }

    public TimerDiceConfiguration(TimerDiceConfiguration configuration) {
        this(configuration.epoch, configuration.first, configuration.interval, configuration.last);
    }

...
```

The missing class is the Contract itself. Here it is:

```java
public class TimerDiceServantContract extends ClientContract{
    private final String CONFIGURATION;
    public final String INTENT_CONFIGURATION;
    public final String STATUS_CONFIGURATION;
    
    public TimerDiceServantContract(String rootContext, String baseClass) {
        this(rootContext, baseClass,null);
    }

    public TimerDiceServantContract(String rootContext, String baseClass, String instance) {
        super(rootContext, baseClass, instance);
        CONFIGURATION="configuration";
        INTENT_CONFIGURATION=INTENT+"/"+CONFIGURATION;
        STATUS_CONFIGURATION=STATUS+"/"+CONFIGURATION;
    }   
}
```

###Start of the Micro-Service Based System
Now what?!
Now there is a fully fledged micro-service based system ready to serve.
Each Service and each Servant can be started by its own!
For convenience reason, here is a main-class that does this all on the same machine:

Start it on any machine as follows, you might change the mqtt-address...:

```java
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

        
        TimerDiceServant timerDiceServant=new TimerDiceServant(mqttURI, computerName);
        
        SimpleGUIService.main(mqttURI.toString());

        System.in.read();
    }
}

```
###How to access the new Micro-Service
Now, you can choose your favorite programming language / environment (Node-Red or MQTT-Lens works fine as well) and can access the Micro-Service / Servants...
Sending to the intent-topic: 

```
Tutorial/Servant/TimerDice/<computerName>/I/configuration
interval: 1000
```


... will make the 'TimerDiceServant' work to intent a 'play' at the 'SimpleDiceService' each second.


The following diagram gives the overview of what has just been done:
<a href="https://github.com/knr1/ch.quantasy.iot.mqtt.gateway.tutorial/blob/master/Micro-service-TimerDice-Full.svg">
<img src="https://github.com/knr1/ch.quantasy.iot.mqtt.gateway.tutorial/blob/master/Micro-service-TimerDice-Full.svg.png" alt="Micro-service-Diagram" />
</a>

###GUI
This is left as an exercise for the user :-)
A NumberGUI is needed, that allows the user to enter a number...
This number is brought to the Micro-Service-System as an event...

A TimerDiceGUIServant will have to combine the TimerDiceServant and the NumberGUIService...

Start these two new Services...
And voilà, you can control the timing with the help of a GUI....
Please notice, you did not even have to stop the system for that.



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

