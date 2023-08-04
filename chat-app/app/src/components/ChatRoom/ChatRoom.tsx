import { useParams } from "react-router-dom"
import { Client } from '@stomp/stompjs'
import { useEffect, useState, useRef } from "react"
import { MessageForm } from "../MessageForm/MessageForm"
import { MessageSection } from "../MessageSection/MessageSection"
import { ChatMessageDTO, ChatMessageJSON } from "../../DTOs/ChatMessageDTO"
import { ClientMessageDTO } from "../../DTOs/ClientMessageDTO"
import { MessageRequestByTimestampDTO } from "../../DTOs/MessageRequestByTimestampDTO"
import { MessageRequestByTimestampType } from "../../DTOs/MessageRequestByTimestampType"
import classes from './ChatRoom.module.css'

export function ChatRoom()
{
	const wasRenderedBefore = useRef(false);
	const roomName: string = useParams().roomName as string;
	const [messages, setMessages] = useState<ChatMessageDTO[]>([]);
	const client = useRef(new Client());
	const initTimestamp = useRef(Date.now());
	const lastRequestedPageNumber = useRef(-1);
	const recievedRequestedMessages = useRef(true);

	// TODO: use CSS modules https://medium.com/@ralph1786/using-css-modules-in-react-app-c2079eadbb87

	function handleRecievedMessage(message: any)
	{
		const receivedMessage: ChatMessageDTO = ChatMessageDTO.fromJSON(JSON.parse(message.body));
		setMessages(oldMessages => [...oldMessages, receivedMessage]);
	}

	function handleRecievedRequestedMessages(messages: any)
	{
		console.log("Handling received requested messages!")
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
		setMessages(oldMessages => [...recievedMessages, ...oldMessages]);
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
					brokerURL: `ws://${window.location.hostname}/websocket`,
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
		<div className={classes.Chat}>

			<div className={classes.RoomName}> {roomName} </div>

			<MessageSection
				messages={messages}
				onScrolledToTop={requestOlderMessages}
			/>

			<MessageForm onSubmit={handleMessageSubmission} />

		</div>
	)
}