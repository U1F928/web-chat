import { NavigateFunction, useNavigate } from 'react-router-dom'
import classes from './JoinChatRoom.module.css'

export function JoinChatRoom()
{
    const navigate: NavigateFunction = useNavigate();

    function handleSubmit(event: React.FormEvent<HTMLFormElement>)
    {
        event.preventDefault();
        const roomNameInput: HTMLInputElement = event.currentTarget.elements.namedItem("room-name-input") as HTMLInputElement;
        const roomName: string = roomNameInput.value;
        navigate(`/${roomName}`);
    }

    return (
        <form className={classes.JoinForm} onSubmit={handleSubmit}>

            <input
                className={classes.RoomNameInput}
                type="text"
                placeholder="Enter a room name"
            />

            <button className={classes.JoinButton}>Join room</button>

        </form>
    );
}