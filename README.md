

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

### Micro-Service source (Model)
<a href="https://github.com/knr1/ch.quantasy.iot.mqtt.gateway.tutorial/blob/master/Micro-service-SimpleDice.svg">
<img src="https://github.com/knr1/ch.quantasy.iot.mqtt.gateway.tutorial/blob/master/Micro-service-SimpleDice.svg.png" alt="Micro-service-Diagram" />
</a>

### Orcherstration: Connecting Micro-Services using Servants
<a href="https://github.com/knr1/ch.quantasy.iot.mqtt.gateway.tutorial/blob/master/Micro-service-SimpleServant-Full.svg">
<img src="https://github.com/knr1/ch.quantasy.iot.mqtt.gateway.tutorial/blob/master/Micro-service-SimpleServant-Full.svg.png" alt="Choreography of the Micro-services" />
</a>


## Servant: Orchestrating Micro-Services
As micro-services are completely agnostic to their surrounding (they feel as they would be completely alone and without broader context). Hence, there is
something needed in order to glue the micro-services together in order to build a working system. This is equivalent to the MVP pattern, where neither the model M nor the view V know each other.
The 'glueing' is done via the presenter P. In the language of event-driven micro-services this is called an orchestrator. In this example here, however,
it is called a servant. These are all other names for 'almost' the same, but these names should explain the context in which the program runs. Here is 'my' logic:
* Services (players/instruments) are controlled and managed by Servants (orchestrators).
* Servants (orchestrators) are controlled and managed by (an) Agent(s) (choreographer(s)).

...

Please note: If Servants are actively cross-orchestring some service-instance(s) (i.e. via their intents), the system will eventually become unmaintainable.
Hint: Try to maintain a clear hierarchy without active-cross-orchestration. Servants might be controlled by 'super-servants'.
Hint: Try not to create a hierarchy that is too deep (i.e. deeper than three levels). The system will become unmaintainable. 


### Tutorials
Please switch in the .git to the Branch: [Simple]


 [Simple]:<https://github.com/knr1/ch.quantasy.iot.mqtt.gateway.tutorial/tree/Simple>
 [SeMqWay]:<https://github.com/knr1/ch.quantasy.mqtt.gateway>
 [Moquette]:<https://github.com/andsel/moquette>
 [MQTT-Lens]:<https://chrome.google.com/webstore/detail/mqttlens/hemojaaeigabkbcookmlgmdigohjobjm?hl=en>
 [tinkerforge]:<http://www.tinkerforge.com/en>
 [MQTT]: <http://mqtt.org/>
 [TiMqWay.jar]: <https://prof.hti.bfh.ch/knr1/TiMqWay.jar>
 [d3Viewer]: <https://github.com/hardillb/d3-MQTT-Topic-Tree>
 [YAML]: <https://en.wikipedia.org/wiki/YAML>
 [micro-service]: <https://en.wikipedia.org/wiki/Microservices>

