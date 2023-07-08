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

function requestMessagesByTimestamp(roomName, creationTimestamp, requestType, messageCountLimit)
{
    let messageRequest = 
    { 
        "creationTimestamp": creationTimestamp, 
        "requestType": requestType, 
        "messageCountLimit": messageCountLimit
    }
    stompClient.send
    (
        "/app/room/" + roomName + "/request_messages_by_timestamp", 
        {}, 
        JSON.stringify(messageRequest)
    );
}

// TODO - add a function to request messages by id
function requestMessagesByID(roomName, id, requestType, messageCountLimit)
{
    let messageRequest = 
    { 
        "id": id, 
        "requestType": requestType, 
        "messageCountLimit": messageCountLimit
    }
    stompClient.send
    (
        "/app/room/" + roomName + "/request_messages_by_id", 
        {}, 
        JSON.stringify(messageRequest)
    );
}