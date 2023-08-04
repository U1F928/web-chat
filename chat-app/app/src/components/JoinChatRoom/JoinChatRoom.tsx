import { NavigateFunction, useNavigate } from 'react-router-dom'
import classes from './JoinChatRoom.module.css'
import { useRef } from "react"

export function JoinChatRoom()
{
    const navigate: NavigateFunction = useNavigate();
    const roomNameInputField = useRef<HTMLInputElement>(null);

    function handleSubmit(event: React.FormEvent<HTMLFormElement>)
    {
        event.preventDefault();
        if(roomNameInputField.current === null) return;
        const roomName: string = roomNameInputField.current.value;
        navigate(`/${roomName}`);
    }

    return (
        <form className={classes.JoinForm} onSubmit={handleSubmit}>

            <input
                ref={roomNameInputField}
                className={classes.RoomNameInput}
                type="text"
                placeholder="Enter a room name"
            />

            <button className={classes.JoinButton}>Join room</button>

        </form>
    );
}