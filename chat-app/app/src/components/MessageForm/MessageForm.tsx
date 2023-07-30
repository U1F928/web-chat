import {useState, useRef} from "react"
import './MessageForm.css'


function MessageForm({onSubmit} : any )
{
	const [messageFormContent, setMessageFormContent] = useState<string>("");
	const messageTextArea = useRef<HTMLTextAreaElement>(null);
	const messageTextAreaRowCount = useRef(1);


	function handleMessageTextAreaContentChange(event : any)
	{
		setMessageFormContent(event.currentTarget.value);
		if(messageTextArea === null) return;
		if(messageTextArea.current === null) return;
		messageTextArea.current.style.height = (messageTextArea.current.scrollHeight) + "px";
	}

    function onSubmitWrapper(event: React.FormEvent<HTMLFormElement>)
    {
		event.preventDefault();
		if(messageTextArea.current === null)
		{
			return;
		}
		const message: string = messageTextArea.current.value;
		messageTextAreaRowCount.current = 1;
        onSubmit(message);
		setMessageFormContent("");
    }

	function handleKeyDown(event : any)
	{
		console.log(event)
		if(event.keyCode === 13 && event.shiftKey)
		{
			return;
		}

		if(event.keyCode === 13)
		{
			event.preventDefault();
			messageTextArea?.current?.form?.requestSubmit();
		}
	}

    return (
		<form id="message-form"  onSubmit={onSubmitWrapper}>
			<textarea 
				form="message-form" 
				id="message-textarea" 
				placeholder="Aa" 
				name="message_text" 
				ref={messageTextArea} 
				rows={messageTextAreaRowCount.current} 
				value={messageFormContent} 
				onChange={handleMessageTextAreaContentChange} 
				onKeyDown={handleKeyDown}
			/>
			<button id="send-button"> </button>
		</form>
    )
}

export default MessageForm