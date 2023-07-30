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
				rows={1} 
				value={messageFormContent} 
				onChange={handleMessageFormContentChange} 
				onKeyDown={handleKeyDown}
			/>
			<button id="send-button"> </button>
		</form>
    )
}

export default MessageForm