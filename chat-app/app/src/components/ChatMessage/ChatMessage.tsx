import { ChatMessageDTO } from "../../DTOs/ChatMessageDTO";

type ChatMessageProps =
{
    message : ChatMessageDTO,
}

export function ChatMessage({message} : ChatMessageProps)
{
    const messageText : string = message.getText();
    const messageID : number = message.getID();
    console.log("message text:" + messageText)
    console.log("message ID: " + messageID)
    
    return (
        <div 
            id={messageID.toString()} 
            className="message" 
            key={messageID}> 
            {messageText}
        </div>
    );
}
