import { useParams } from "react-router-dom"
import { Client } from '@stomp/stompjs'
import { useEffect, useState, useRef } from "react"
import { ChatMessage } from '../ChatMessage/ChatMessage'
import { MessageForm } from "../MessageForm/MessageForm"
import { MessageSection } from "../MessageSection/MessageSection"
import { ChatMessageDTO, ChatMessageJSON } from "../../DTOs/ChatMessageDTO"
import { ClientMessageDTO } from "../../DTOs/ClientMessageDTO"
import { MessageRequestByTimestampDTO } from "../../DTOs/MessageRequestByTimestampDTO"
import { MessageRequestByTimestampType } from "../../DTOs/MessageRequestByTimestampType"
import './ChatRoom.css'

function ChatRoom()
{
	const wasRenderedBefore = useRef(false);

	const roomName: string = useParams().roomName as string;

	const [messages, setMessages] = useState<JSX.Element[]>([]);

	// client related variables
	const client = useRef(new Client());
	const initTimestamp = useRef(Date.now());
	const lastRequestedPageNumber = useRef(-1);
	const recievedRequestedMessages = useRef(true);

	// TODO: use CSS modules https://medium.com/@ralph1786/using-css-modules-in-react-app-c2079eadbb87

	function handleRecievedMessage(message: any)
	{
		console.log(message)
		const receivedMessage: ChatMessageDTO = ChatMessageDTO.fromJSON(JSON.parse(message.body));
		const newMessageElement = <ChatMessage message={receivedMessage} />;
		setMessages(oldMessages => [...oldMessages, newMessageElement]);
	}

	function handleRecievedRequestedMessages(messages: any)
	{
		/*
			Assuming only older messages are requested.
			Before requesting anything else other than 
			older messages implement the issue 48 (More informative DTOs).
			https://github.com/U1F928/web-chat-2/issues/48
		*/
		const recievedMessages: ChatMessageDTO[] = JSON.parse(messages.body).map
			(
				(element: ChatMessageJSON) => 
				{
					return ChatMessageDTO.fromJSON(element);
				}
			);
		if (recievedMessages.length === 0)
		{
			return;
		}
		recievedRequestedMessages.current = true;
		let newMessageElements: JSX.Element[] = [];
		for (let i = 0; i < recievedMessages.length; i++)
		{
			const newMessageElement = <ChatMessage message={recievedMessages[i]} />;
			newMessageElements.push(newMessageElement);
		}
		setMessages(oldMessages => [...newMessageElements, ...oldMessages]);
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
				handleRecievedRequestedMessages
			);
		requestOlderMessages();
	}

	function initializeConnection()
	{
		client.current = new Client
			(
				{
					//brokerURL: `ws://${window.location.hostname}/websocket`,
					brokerURL: `ws://localhost:8080/websocket`,
					debug: (str: string) => { console.log(str) }
				}
			);
		client.current.onConnect = handleConnect;
		client.current.activate();
	}

	function handleMessageSubmission(message: string)
	{
		if (!client.current.connected)
		{
			return;
		}
		const clientMessage = new ClientMessageDTO(message);
		client.current.publish({ destination: `/app/room/${roomName}/publish_message`, body: JSON.stringify(clientMessage) })
	}

	function requestOlderMessages()
	{
		if (!client.current.connected)
		{
			return;
		}
		// if previous request is not fulfilled yet
		if (!recievedRequestedMessages.current)
		{
			return;
		}

		lastRequestedPageNumber.current += 1;
		const pageSize: number = 15;

		let messageRequest: MessageRequestByTimestampDTO = new MessageRequestByTimestampDTO
			(
				initTimestamp.current,
				MessageRequestByTimestampType.LESS_THAN_TIMESTAMP,
				pageSize,
				lastRequestedPageNumber.current
			)

		client.current.publish
			(
				{
					destination: `/app/room/${roomName}/request_messages_by_timestamp`,
					body: JSON.stringify(messageRequest)
				}
			)

		recievedRequestedMessages.current = false;
	}

	useEffect
		(
			function _()
			{
				if (wasRenderedBefore.current === false)
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