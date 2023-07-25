import { useParams } from "react-router-dom"
import './ChatRoom.css'

function Chat()
{
	let roomName : string = useParams().roomName as string;
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

export default Chat