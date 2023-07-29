import { useParams } from "react-router-dom"
import './ChatRoom.css'
import { Client } from '@stomp/stompjs'
import { useEffect, useState, useRef, createElement } from "react"
import MessageForm from "../MessageForm/MessageForm"
import MessageSection from "../MessageSection/MessageSection"

function ChatRoom()
{
	let wasRenderedBefore = useRef(false);

	let roomName: string = useParams().roomName as string;

	const [messages, setMessages] = useState<any[]>([]);

	// client related variables
	let client = useRef(new Client());
	let initTimestamp = useRef(Date.now());
	let lastRequestedPageNumber = useRef(-1);
	let recievedRequestedMessages = useRef(true);

	// TODO: add send by hitting enter functionality
	//
	// TODO: use CSS modules https://medium.com/@ralph1786/using-css-modules-in-react-app-c2079eadbb87
	//
	// TODO: add classes for the DTOs
	//
	// TODO: sort in the messages by ID
	//
	// TODO: check that missed messages load after disconnect&reconnect
	function createNewMessageElement(message : any)
	{
		let messageText = message.text;
		let messageID = message.id;
		let messageElement = createElement( 'div', { id: messageID, className: 'message', key : messageID}, messageText);
		return messageElement;
	}
	function handleConnect() 
	{
		client.current.subscribe
		(
			`/topic/room.${roomName}`,
			function handleRecievedMessage(message : any)
			{
				let receivedMessage = JSON.parse(message.body);
				let newMessageElement = createNewMessageElement(receivedMessage);
				//https://stackoverflow.com/questions/59322030/why-is-react-statearray-empty-inside-callback-function-why-is-it-not-using-th
				// use state updater function
				function updateMessages(oldMessages : any)
				{
					return [...oldMessages, newMessageElement];
				}
				setMessages(updateMessages);
				console.log(`Received: ${message.body}`);
			}
		);
		client.current.subscribe
		(
			`/user/topic/requested_messages`,
			function handleRecievedMessage(message : any)
			{
				let recievedMessages = JSON.parse(message.body);
				if(recievedMessages.length === 0)
				{
					return;
				}
				console.log(recievedMessages);
				recievedRequestedMessages.current = true;
				let newMessageElements : any[] = [];
				for(let i = 0; i < recievedMessages.length; i++)
				{
					let newMessageElement = createNewMessageElement(recievedMessages[i]);
					newMessageElements.push(newMessageElement);
				}
				//https://stackoverflow.com/questions/59322030/why-is-react-statearray-empty-inside-callback-function-why-is-it-not-using-th
				// use state updater function
				function updateMessages(oldMessages : any)
				{
					return [newMessageElements, ...oldMessages];
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
		if(!client.current.connected)
		{
			return;
		}
		let clientMessage = { "text": message };
		client.current.publish({ destination: `/app/room/${roomName}/publish_message`, body: JSON.stringify(clientMessage) })
	}

	function handleScrolledToTop()
	{
		console.log("scrolled to top");
		if(!client.current.connected)
		{
			return;
		}
		// if previous request is not fulfilled yet
		if(!recievedRequestedMessages.current)
		{
			return;
		}

		recievedRequestedMessages.current = false;
		lastRequestedPageNumber.current += 1;
		const pageSize : number = 15;
        let messageRequest =
        {
            "creationTimestamp": initTimestamp.current,
            "requestType": "LESS_THAN_TIMESTAMP",
            "pageSize": pageSize,
			"pageNumber": lastRequestedPageNumber.current
        }
		client.current.publish
		(
			{ 
				destination: `/app/room/${roomName}/request_messages_by_timestamp`, 
				body: JSON.stringify(messageRequest) 
			}
		)
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

			<MessageSection messages={messages} onScrolledToTop={handleScrolledToTop} />

		  	<MessageForm onSubmit={handleMessageSubmission} />

		</div>
	)
}

export default ChatRoom