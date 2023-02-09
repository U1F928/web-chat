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
    });
}

function sendMessage(roomName, text)
{
    stompClient.send("/app/room/"+ roomName + "/publish", {}, JSON.stringify({"text":text}));
}