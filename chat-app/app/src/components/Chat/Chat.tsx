import { useParams } from "react-router-dom"
import './Chat.css'

// TODO: add routing, i.e. /room/Cats calls 'Chat("Cats")'
function Chat()
{
	let {roomName} = useParams();
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
				<input id="comment-form" type="text" name="comment_text" placeholder="Aa"/> 
				<button id="send-button"> &#10140; </button>
			</span>
		</div>
    )
}

export default Chat