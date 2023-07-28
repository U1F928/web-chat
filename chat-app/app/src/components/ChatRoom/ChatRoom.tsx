import { useParams } from "react-router-dom"
import './ChatRoom.css'
import { Client } from '@stomp/stompjs'
import { useEffect, useState, useRef, createElement } from "react"
import MessageForm from "../MessageForm/MessageForm"
import MessageSection from "../MessageSection/MessageSection"

function ChatRoom()
{
	let roomName: string = useParams().roomName as string;
	const [messages, setMessages] = useState<any[]>([]);
	let client = useRef(new Client());
	let wasRenderedBefore = useRef(false);
	// TODO: add functionality to load older messages via requests by timestamp
	//
	// TODO: add send by hitting enter functionality
	//
	// TODO: use CSS modules https://medium.com/@ralph1786/using-css-modules-in-react-app-c2079eadbb87
	function handleConnect() 
	{
		client.current.subscribe
		(
			`/topic/room.${roomName}`,
			function handleRecievedMessage(message : any)
			{
				let messageText = JSON.parse(message.body)["text"];
				let messageID = JSON.parse(message.body)["id"];
				let messageElement = createElement( 'div', { className: 'message', key : messageID}, messageText);
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

	function initializeConnection()
	{
		client.current = new Client
		(
			{
				brokerURL: 'ws://localhost:8080/websocket',
				debug: function handleDebug(str : string) 
				{
					console.log(str);
				}
			}
		);
		client.current.onConnect = handleConnect;
		client.current.activate();
		console.log("initializing...");
	}
	function handleMessageSubmission(message : string)
	{
		let clientMessage = { "text": message };
		client.current.publish({ destination: `/app/room/${roomName}/publish_message`, body: JSON.stringify(clientMessage) })
	}

	function handleScrollToTop()
	{
		console.log("scrolled to top");
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

			<MessageSection messages={messages} onScrollToTop={handleScrollToTop} />

		  	<MessageForm onSubmit={handleMessageSubmission} />

		</div>
	)
}

export default ChatRoom