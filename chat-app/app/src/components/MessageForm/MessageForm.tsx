import { useState, useRef, useEffect } from "react"
import classes from './MessageForm.module.css'
import sendIcon from './send-icon-green.svg'

type MessageFormProps =
	{
		onSubmit: (message: string) => void
	}

export function MessageForm({ onSubmit }: MessageFormProps)
{
	const [messageFormContent, setMessageFormContent] = useState<string>("");
	const messageTextArea = useRef<HTMLTextAreaElement>(null);
	const messageTextAreaRowCount = useRef(1);
	const sendButton = useRef<HTMLButtonElement>(null);

	function resizeMessageTextArea()
	{
		if (messageTextArea === null) return;
		if (messageTextArea.current === null) return;
		messageTextArea.current.style.height = "0px";
		messageTextArea.current.style.height = (messageTextArea.current.scrollHeight) + "px";
	}

	function handleMessageTextAreaContentChange(event: React.ChangeEvent<HTMLTextAreaElement>)
	{
		setMessageFormContent(event.currentTarget.value);
		resizeMessageTextArea();
	}

	function onSubmitWrapper(event: React.FormEvent<HTMLFormElement>)
	{
		event.preventDefault();
		if (messageTextArea.current === null)
		{
			return;
		}
		const message: string = messageTextArea.current.value;
		onSubmit(message);
		setMessageFormContent("");
	}


	function handleKeyDown(event: React.KeyboardEvent<HTMLTextAreaElement>)
	{
		if (event.key === 'Enter' && event.shiftKey)
		{
			return;
		}

		if (event.key === 'Enter')
		{
			event.preventDefault();
			// Request submit is not supported on Safari 15.6
			// https://stackoverflow.com/questions/67000944/react-form-ref-issue-in-safari-while-using-useref
			// https://developer.mozilla.org/en-US/docs/Web/API/HTMLFormElement/requestSubmit
			//messageTextArea?.current?.form?.requestSubmit();

			// Use click() instead
			sendButton?.current?.click();
		}
	}

	useEffect(resizeMessageTextArea);

	return (
		<form className={classes.MessageForm} onSubmit={onSubmitWrapper}>

			<textarea
				className={classes.MessageTextArea}
				placeholder="Aa"
				name="message_text"
				ref={messageTextArea}
				rows={messageTextAreaRowCount.current}
				value={messageFormContent}
				onChange={handleMessageTextAreaContentChange}
				onKeyDown={handleKeyDown}
			/>

			<button className={classes.SendButton} ref={sendButton} > 
				<img className={classes.SendIcon} src={sendIcon} />
			</button>

		</form>
	)
}