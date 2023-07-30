import {useState, useRef, useEffect} from "react"
import './MessageForm.css'
import sendIcon from './send-icon.svg'


function MessageForm({onSubmit} : any )
{
	const [messageFormContent, setMessageFormContent] = useState<string>("");
	const messageTextArea = useRef<HTMLTextAreaElement>(null);
	const messageTextAreaRowCount = useRef(1);

	function resizeMessageTextArea()
	{
		if(messageTextArea === null) return;
		if(messageTextArea.current === null) return;
		messageTextArea.current.style.height = "0px";
		messageTextArea.current.style.height = (messageTextArea.current.scrollHeight) + "px";
	}

	function handleMessageTextAreaContentChange(event : any)
	{
		setMessageFormContent(event.currentTarget.value);
		resizeMessageTextArea();
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

	useEffect(resizeMessageTextArea);

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
			<input id="send-button" type="image" src={sendIcon} />
		</form>
    )
}

export default MessageForm