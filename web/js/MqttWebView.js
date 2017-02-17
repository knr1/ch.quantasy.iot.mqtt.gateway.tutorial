var mqtt;
var reconnectTimeout = 2000;
var clientName = "gui" + parseInt((Math.random() * 10000), 10).toString(32);

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
    mqtt.connect(options);

}


function onConnect() {
    $('#status').val('Connected to ' + host + ':' + port + path);
    // Connection succeeded; subscribe to our topic
    intentTopic = baseTopic + "/I/#";
    mqtt.subscribe(intentTopic,{qos:1});
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
    //
    // $('#ws').prepend('<li>' + topic + ' = ' + payload + '</li>');
}
;

function testTopic(topic, intent) {
    regex = new RegExp(baseTopic + "/intent"+"(/.*)*"+"/"+intent + "(/.*)*");
    return topic.match(regex);
}


function sendEvent(topic, object) {
    var event = {
        "timestamp": Math.floor((new Date).getTime() / 1000)
    };
    event.value = object;
    var yaml = json2yaml([event]);
    yaml = "---\n" + yaml;
    console.log("to topic: " + baseTopic + " sending text: " + yaml);
    message = new Paho.MQTT.Message(yaml);
    message.destinationName = baseTopic + "/" + clientName + "/E/" + topic;
    message.retained = true;
    mqtt.send(message);
}
function sendStatus(topic, object) {
    var yaml = json2yaml(object);
    yaml = "---\n" + yaml;
    console.log("to topic: " + baseTopic + " sending text: " + yaml);
    message = new Paho.MQTT.Message(yaml);
    message.destinationName = baseTopic + "/" + clientName + "/S/" + topic;
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
