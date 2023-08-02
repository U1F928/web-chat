import { useParams } from "react-router-dom"
import './ChatRoom.css'
import { Client } from '@stomp/stompjs'
import { useEffect, useState, useRef, createElement } from "react"
import MessageForm from "../MessageForm/MessageForm"
import MessageSection from "../MessageSection/MessageSection"

function ChatRoom()
{
	const wasRenderedBefore = useRef(false);

	const roomName: string = useParams().roomName as string;

	const [messages, setMessages] = useState<any[]>([]);

	// client related variables
	const client = useRef(new Client());
	const initTimestamp = useRef(Date.now());
	const lastRequestedPageNumber = useRef(-1);
	const recievedRequestedMessages = useRef(true);

	// TODO: use CSS modules https://medium.com/@ralph1786/using-css-modules-in-react-app-c2079eadbb87
	//
	// TODO: add classes for the DTOs
	function createNewMessageElement(message : any)
	{
		const messageText = message.text;
		const messageID = message.id;
		const messageElement = createElement( 'div', { id: messageID, className: 'message', key : messageID}, messageText);
		return messageElement;
	}

	function handleRecievedMessage(message : any)
	{
		const receivedMessage = JSON.parse(message.body);
		const newMessageElement = createNewMessageElement(receivedMessage);
		//https://stackoverflow.com/questions/59322030/why-is-react-statearray-empty-inside-callback-function-why-is-it-not-using-th
		// use state updater function
		setMessages(oldMessages => [...oldMessages, newMessageElement]);
	}

	function handleRecievedRequestedMessage(message : any)
	{
		/*
		 	Assuming only older messages are requested.
		 	Before requesting anything else other than 
			older messages implement the issue 48 (More informative DTOs).
		 	https://github.com/U1F928/web-chat-2/issues/48
		*/
		const recievedMessages = JSON.parse(message.body);
		if(recievedMessages.length === 0)
		{
			return;
		}
		recievedRequestedMessages.current = true;
		let newMessageElements : any[] = [];
		for(let i = 0; i < recievedMessages.length; i++)
		{
			const newMessageElement = createNewMessageElement(recievedMessages[i]);
			newMessageElements.push(newMessageElement);
		}
		setMessages(oldMessages => [newMessageElements, ...oldMessages]);
	}

	function handleConnect() 
	{
		client.current.subscribe
		(
			`/topic/room.${roomName}`,
			handleRecievedMessage
		);
		client.current.subscribe
		(
			`/user/topic/requested_messages`,
			handleRecievedRequestedMessage
		);
		requestOlderMessages();
	}

	function initializeConnection()
	{
		client.current = new Client
		(
			{
				brokerURL: `ws://${window.location.hostname}/websocket`,
			}
		);
		client.current.onConnect = handleConnect;
		client.current.activate();
	}

	function handleMessageSubmission(message : string)
	{
		if(!client.current.connected)
		{
			return;
		}
		const clientMessage = { "text": message };
		client.current.publish({ destination: `/app/room/${roomName}/publish_message`, body: JSON.stringify(clientMessage) })
	}

	function requestOlderMessages()
	{
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
        const messageRequest =
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

			<MessageSection messages={messages} onScrolledToTop={requestOlderMessages} />

		  	<MessageForm onSubmit={handleMessageSubmission} />

		</div>
	)
}

export default ChatRoom