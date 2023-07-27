import { useParams } from "react-router-dom"
import './ChatRoom.css'
import { Client } from '@stomp/stompjs';
import { useEffect, useState, useRef, createElement } from "react";

function ChatRoom()
{
	let roomName: string = useParams().roomName as string;
	const [messageFormText, setMessageFormText] = useState<string>("");
	let client = useRef(new Client());
	let wasRenderedBefore = useRef(false);
	// TODO: add functionality to load older messages via requests by timestamp
	//
	// TODO: add send by hitting enter functionality

	const [messages, setMessages] = useState<any[]>([]);
	function initializeConnection()
	{
		client.current = new Client
			(
				{
					brokerURL: 'ws://localhost:8080/websocket',
					debug: function handleDebug(str : string) 
					{
						console.log(str);
					},
					onConnect: function handleConnect() 
					{
						client.current.subscribe
						(
							'/topic/room.' + roomName,
							function handleNewMessage(message : any)
							{
								let messageText = JSON.parse(message.body)["text"];
								let messageElement = createElement( 'div', { className: 'message' }, messageText);
								//https://stackoverflow.com/questions/59322030/why-is-react-statearray-empty-inside-callback-function-why-is-it-not-using-th
								// use state updater function
								function updateMessages(oldMessages : any)
								{
									return [...oldMessages, messageElement];
								}
								setMessages(updateMessages);
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
		console.log(messages);
		event.preventDefault();
		const messageInput: HTMLInputElement = event.currentTarget.elements.namedItem("message-form") as HTMLInputElement;
		const message: string = messageInput.value;
		//let messageElement = createElement( 'div', { className: 'message' }, message);
		let clientMessage = { "text": message };
		client.current.publish({ destination: "/app/room/" + roomName + "/publish_message", body: JSON.stringify(clientMessage) })
		setMessageFormText("");
		//setMessages([...messages, messageElement]);
	}
	function handleChange(event : any)
	{
		setMessageFormText(event.currentTarget.value);
	}
	useEffect
	(
		function _()
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
				{messages}
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