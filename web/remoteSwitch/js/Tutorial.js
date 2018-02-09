var texts;
var buttons;
var svgDoc;
var visual;

$(window).on("load", function () {
//$(document).ready(function () {
//alert("Document loaded, including graphics and embedded documents (like SVG)");
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
    sendDescription("I", {"id": "text", "content": "text"});
    sendDescription("S/visibility", {"state": "visible hidden"});
    sendDescription("E/button", {"timestamp": "[0..]", "value": {"buttonId": "text", "action": "clicked"}});

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
    console.log("payload[payload.length-1].id="+payload[payload.length-1].id);
    for (var i = 0; i < texts.length; i++) {
        var text = texts[i];
        var textID = text.getAttribute("textId");
        if (textID === (payload[payload.length-1].id)) {
            content = payload[payload.length-1].content;
            text.textContent = content;
        }
    }
}
// $('#ws').prepend('<li>' + topic + ' = ' + payload + '</li>');
