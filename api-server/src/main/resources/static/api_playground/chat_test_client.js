class ChatTestClient
{
    async init()
    {
        var socket = new SockJS('/websocket');
        this.stompClient = Stomp.over(socket);
        /*
            https://stackoverflow.com/questions/47550598/websocket-using-sockjs-client-and-stompjs-cannot-connect-if-i-do-not-connect-imm
            Once a STOMP client is created, it must call its connect() method 
            to effectively connect and authenticate to the STOMP server.
        */
        let result = await this.connectPromisified(this.stompClient);
        if(result === "Failed to connect!")
        {
            throw new Error(result);
        }
        this.subscribeToRequestedMessages();
    }

    // Next function wraps the above API call into a Promise
    // and handles the callbacks with resolve and reject.

    //function apiFunctionWrapper(query)
    //{
    //    return new Promise
    //    (
    //        (resolve, reject) =>
    //        {
    //            apiFunction
    //            (
    //                query, 
    //                (successResponse) =>
    //                {   
    //                    resolve(successResponse);
    //                }, 
    //                (errorResponse) =>
    //                {
    //                    reject(errorResponse);
    //                }
    //            );
    //        }
    //    );
    //}
    // https://stackoverflow.com/a/51163205/7215755
    connectPromisified(stompClient)
    {
        return new Promise
        (
            (resolve, reject) =>
            {
                stompClient.connect
                (
                    "",
                    "",
                    () => 
                    {
                        resolve("Connected!")
                    },
                    () =>
                    {
                        reject("Failed to connect!")
                    }
                )
            }
        )
    }

    subscribeToRoom(roomName)
    {
        let subscription = this.stompClient.subscribe('/topic/room/' + roomName, function (greeting)
        {
            console.log(JSON.parse(greeting.body).content);
        });
        return subscription;
    }

    subscribeToRequestedMessages()
    {
        let subscription = this.stompClient.subscribe('/user/topic/requested_messages', function (greeting)
        {
            console.log(JSON.parse(greeting.body).content);
        });
        return subscription;
    }

    sendMessage(roomName, text)
    {
        let clientMessage = { "text": text };
        this.stompClient.send
            (
                "/app/room/" + roomName + "/publish_message",
                {},
                JSON.stringify(clientMessage)
            );
    }
    requestMessagesByTimestamp(roomName, creationTimestamp, requestType, messageCountLimit)
    {
        let messageRequest =
        {
            "creationTimestamp": creationTimestamp,
            "requestType": requestType,
            "messageCountLimit": messageCountLimit
        }
        this.stompClient.send
            (
                "/app/room/" + roomName + "/request_messages_by_timestamp",
                {},
                JSON.stringify(messageRequest)
            );
    }

    requestMessagesByID(roomName, id, requestType, messageCountLimit)
    {
        let messageRequest =
        {
            "id": id,
            "requestType": requestType,
            "messageCountLimit": messageCountLimit
        }
        this.stompClient.send
            (
                "/app/room/" + roomName + "/request_messages_by_id",
                {},
                JSON.stringify(messageRequest)
            );
    }
}









