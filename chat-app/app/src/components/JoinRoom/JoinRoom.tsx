import './JoinRoom.css'
function JoinRoom(_ : { children: string; })
{
    function handleSubmit(event : any)
    {
        event.preventDefault();
        console.log("Submit handled");
        const  roomName = event.target.elements["room-name-input"];
        console.log(roomName.value);
        // TODO: Route to the Chat component
    }
    return (
        <form id="join-form" onSubmit={handleSubmit}> 
            <input id="room-name-input" type="text" placeholder="Enter a room name"></input>
            <button id="join-button">Join room </button>
        </form>
    );
}

export default JoinRoom