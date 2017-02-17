

# MQTT-Gateway
ch.quantasy.iot.mqtt.gateway.tutorial

This tutorial introduces one possible way of how to program within the world of micro-services.

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
Tutorial/SimpleDice/<computerName>/play
```

... will make the service work.

When the service has done something, it will respond on the event-topic:
```
Tutorial/SimpleDice/<computerName>/play
```

Go ahead and subscribe on that topic! (Remember: All events are always sent within an array.)

If you want to know the status of the micro-service subscribe to the following status-topic:
```
Tutorial/SimpleDice/<computerName>/status/#
```

You will always receive the latest status the micro-service is operating in.


The following diagram gives the overview of what has just been done:
<a href="https://github.com/knr1/ch.quantasy.iot.mqtt.gateway.tutorial/blob/master/Micro-service-SimpleDice-Full.svg">
<img src="https://github.com/knr1/ch.quantasy.iot.mqtt.gateway.tutorial/blob/master/Micro-service-SimpleDice-Full.svg.png" alt="Micro-service-Diagram" />
</a>






 [SeMqWay]:<https://github.com/knr1/ch.quantasy.mqtt.gateway>
 [Moquette]:<https://github.com/andsel/moquette>
 [MQTT-Lens]:<https://chrome.google.com/webstore/detail/mqttlens/hemojaaeigabkbcookmlgmdigohjobjm?hl=en>
 [tinkerforge]:<http://www.tinkerforge.com/en>
 [MQTT]: <http://mqtt.org/>
 [TiMqWay.jar]: <https://prof.hti.bfh.ch/knr1/TiMqWay.jar>
 [d3Viewer]: <https://github.com/hardillb/d3-MQTT-Topic-Tree>
 [YAML]: <https://en.wikipedia.org/wiki/YAML>
 [micro-service]: <https://en.wikipedia.org/wiki/Microservices>

