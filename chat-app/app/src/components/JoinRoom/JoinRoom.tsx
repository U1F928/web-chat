import './JoinRoom.css'
import { useNavigate } from 'react-router-dom'

function JoinRoom()
{
    let navigate = useNavigate();
    function handleSubmit(event : any)
    {
        event.preventDefault();
        console.log("Submit handled");
        const  roomName = event.target.elements["room-name-input"];
        console.log(roomName.value);
        // TODO: Route to the Chat component
        navigate(`/${roomName}`);
    }
    return (
        <form id="join-form" onSubmit={handleSubmit}> 
            <input id="room-name-input" type="text" placeholder="Enter a room name"/>
            <button id="join-button">Join room </button>
        </form>
    );
}

export default JoinRoom