import {useState} from "react"
import './MessageForm.css'


function MessageForm({onSubmit} : any )
{
	const [messageFormContent, setMessageFormContent] = useState<string>("");
	function handleMessageFormContentChange(event : any)
	{
		setMessageFormContent(event.currentTarget.value);
	}

    function onSubmitWrapper(event: React.FormEvent<HTMLFormElement>)
    {
		event.preventDefault();
		const messageInput: HTMLInputElement = event.currentTarget.elements.namedItem("message-textarea") as HTMLInputElement;
		const message: string = messageInput.value;
        onSubmit(message);
		setMessageFormContent("");
    }

    return (
		<form id="message-form" onSubmit={onSubmitWrapper}>
			<textarea form="message-form" id="message-textarea" rows={1} name="message_text" placeholder="Aa" onChange={handleMessageFormContentChange} value={messageFormContent} />
			<button id="send-button"> </button>
		</form>
    )
}

export default MessageForm