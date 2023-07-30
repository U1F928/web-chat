import {useState, useRef} from "react"
import './MessageForm.css'


function MessageForm({onSubmit} : any )
{
	const [messageFormContent, setMessageFormContent] = useState<string>("");
	const messageTextArea = useRef<HTMLTextAreaElement>(null);
	function handleMessageFormContentChange(event : any)
	{
		setMessageFormContent(event.currentTarget.value);
	}

    function onSubmitWrapper(event: React.FormEvent<HTMLFormElement>)
    {
		event.preventDefault();
		if(messageTextArea.current === null)
		{
			return;
		}
		const message: string = messageTextArea.current.value;
        onSubmit(message);
		setMessageFormContent("");
    }

    return (
		<form id="message-form" onSubmit={onSubmitWrapper}>
			<textarea form="message-form" id="message-textarea" ref={messageTextArea} rows={1} name="message_text" placeholder="Aa" onChange={handleMessageFormContentChange} value={messageFormContent} />
			<button id="send-button"> </button>
		</form>
    )
}

export default MessageForm