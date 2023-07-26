import { useParams } from "react-router-dom"
import './ChatRoom.css'
import { Client } from '@stomp/stompjs';
import { useEffect } from "react";

function ChatRoom()
{
	let roomName : string = useParams().roomName as string;
	let client : Client;
	function initializeConnection()
	{
		client = new Client
		(
			{
				brokerURL: 'ws://localhost:8080/websocket',
				debug: (str) => 
				{
					console.log(str);
				},
				onConnect: () => 
				{
					client.subscribe
					(
						'/topic/room.' + roomName, 
						(message) =>
						{
							console.log(`Received: ${message.body}`);
						}
					);
				}
			}
		);
	
		client.activate();
		console.log("initializing...");
	}
    function handleSubmit(event: React.FormEvent<HTMLFormElement>)
    {
        event.preventDefault();
        const messageInput : HTMLInputElement = event.currentTarget.elements.namedItem("message-form") as HTMLInputElement;
        const message : string  = messageInput.value;
        let clientMessage = { "text": message };
		client.publish({ destination: "/app/room/" + roomName + "/publish_message", body: JSON.stringify(clientMessage) })
    }
	useEffect(initializeConnection);
    return(
        <div id="chat">
			<div id="room-name">
  	          {roomName}
			</div>

			<div id="message-section">
  	      {/*
				<img src="/static/chat/loading_icon.svg" alt="Loading..." id="loading-icon">
  	          </img>
  	      */}
			</div>

        	<form id="send-message" onSubmit={handleSubmit}>
				<textarea form="send-message" id="message-form" rows={1} name="message_text" placeholder="Aa"/> 
				<button id="send-button"> </button>
        	</form>

		</div>
    )
}

export default ChatRoom