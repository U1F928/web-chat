import { useParams } from "react-router-dom"
import './ChatRoom.css'
import { Client } from '@stomp/stompjs';
import { useEffect, useState, useRef } from "react";

function ChatRoom()
{
	let roomName: string = useParams().roomName as string;
	const [messageFormText, setMessageFormText] = useState("");
	let client = useRef(new Client());
	let wasRenderedBefore = useRef(false);
	// TODO: use 'state' to represent the message-section, 
	//  	 also use 'state' to represent the message-form textarea
	// TODO: add functionality to load older messages via requests by timestamp
	//
	// TODO: add send by hitting enter functionality
	function initializeConnection()
	{
		client.current = new Client
			(
				{
					brokerURL: 'ws://localhost:8080/websocket',
					debug: (str) => 
					{
						console.log(str);
					},
					onConnect: () => 
					{
						client.current.subscribe
							(
								'/topic/room.' + roomName,
								(message : any) =>
								{
									console.log(`Received: ${message.body}`);
								}
							);
					}
				}
			);

		client.current.activate();
		console.log("initializing...");
	}
	function handleSubmit(event: React.FormEvent<HTMLFormElement>)
	{
		event.preventDefault();
		const messageInput: HTMLInputElement = event.currentTarget.elements.namedItem("message-form") as HTMLInputElement;
		const message: string = messageInput.value;
		let clientMessage = { "text": message };
		client.current.publish({ destination: "/app/room/" + roomName + "/publish_message", body: JSON.stringify(clientMessage) })
		setMessageFormText("");
	}
	function handleChange(event : any)
	{
		setMessageFormText(event.currentTarget.value);
	}
	useEffect
	(
		() =>
		{
			console.log("was rendered before:" + wasRenderedBefore.current);
			if(wasRenderedBefore.current === false)
			{
				initializeConnection();
				wasRenderedBefore.current = true;
			}
		}
	);
	return (
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
				<textarea form="send-message" id="message-form" rows={1} name="message_text" placeholder="Aa" onChange={handleChange} value={messageFormText} />
				<button id="send-button"> </button>
			</form>

		</div>
	)
}

export default ChatRoom