import './JoinChatRoom.css'
import { NavigateFunction, useNavigate } from 'react-router-dom'

function JoinRoom()
{
    const navigate : NavigateFunction = useNavigate();
    function handleSubmit(event: React.FormEvent<HTMLFormElement>)
    {
        event.preventDefault();
        const roomNameInput : HTMLInputElement = event.currentTarget.elements.namedItem("room-name-input") as HTMLInputElement;
        const roomName : string  = roomNameInput.value;
        navigate(`/${roomName}`);
    }
    return (
        <form id="join-form" onSubmit={handleSubmit}>
            <input id="room-name-input" type="text" placeholder="Enter a room name" />
            <button id="join-button">Join room </button>
        </form>
    );
}

export default JoinRoom