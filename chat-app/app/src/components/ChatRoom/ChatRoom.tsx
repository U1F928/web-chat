import { useParams } from "react-router-dom"
import './ChatRoom.css'
import { Client } from '@stomp/stompjs';
import { useEffect } from "react";

function ChatRoom()
{
	let roomName : string = useParams().roomName as string;
	function initializeConnection()
	{
		const client = new Client({
            brokerURL: 'ws://localhost:8080/websocket',
            debug: (str) => {
                console.log(str);
            },
        });
		// @ts-ignore
		client.onConnect( () => {
            console.log('onConnect');
            client.subscribe('/topic/balance', message => {
                console.log(message);
            })
        });
		client.activate();
		console.log("initializing...");
	}
	useEffect(initializeConnection);
    return(
        <div id="chat">
			<div id="room-name">
  	          {roomName}
			</div>

			<div id="comment-section">
  	      {/*
				<img src="/static/chat/loading_icon.svg" alt="Loading..." id="loading-icon">
  	          </img>
  	      */}
			</div>

			<span id="send-comment">
				<textarea id="comment-form" rows={1} name="comment_text" placeholder="Aa"/> 
				<button id="send-button"> </button>
			</span>
		</div>
    )
}

export default ChatRoom