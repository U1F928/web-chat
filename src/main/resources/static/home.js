function joinRoom(roomName)
{
    var socket = new SockJS('/websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame)
    {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/room/' + roomName, function (greeting)
        {
            console.log(JSON.parse(greeting.body).content);
        });
        stompClient.subscribe('/user/topic/requested_messages', function (greeting)
        {
            console.log(JSON.parse(greeting.body).content);
        });

    });
}

function sendMessage(roomName, text)
{
    let clientMessage = {"text":text};
    stompClient.send
    (
        "/app/room/" + roomName + "/publish_message", 
        {}, 
        JSON.stringify(clientMessage)
    );
}

function requestMessages(roomName, unixTimestamp, requestType, messageCount)
{
    let messageRequest = 
    { 
        "unixTimestamp": unixTimestamp, 
        "requestType": requestType, 
        "messageCount": messageCount
    }
    stompClient.send
    (
        "/app/room/" + roomName + "/request_messages", 
        {}, 
        JSON.stringify(messageRequest)
    );
}