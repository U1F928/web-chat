import { ChatMessageDTO } from "../../DTOs/ChatMessageDTO";
import classes from './ChatMessage.module.css'

type ChatMessageProps =
    {
        message: ChatMessageDTO,
    }

export function ChatMessage({ message }: ChatMessageProps)
{
    const messageText: string = message.getText();
    const messageID: number = message.getID();

    return (
        <div
            id={messageID.toString()}
            className={classes.ChatMessage}
            key={messageID}>
            {messageText}
        </div>
    );
}
