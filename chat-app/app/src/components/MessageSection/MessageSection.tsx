import { useEffect, useRef } from 'react';
import { ChatMessage } from '../ChatMessage/ChatMessage';
import { ChatMessageDTO } from '../../DTOs/ChatMessageDTO';
import classes from './MessageSection.module.css'

type MessageSectionProps =
    {
        messages: ChatMessageDTO[],
        onScrolledToTop: () => void
    }

export function MessageSection({ messages, onScrolledToTop }: MessageSectionProps)
{
    const messageSection = useRef<HTMLDivElement>(null);
    const previousDistFromBottom = useRef(0);
    const scrolledToBottom = useRef(true);
    const firstMessageID = useRef(-1);

    function checkIfScrolledToTop()
    {
        if (messageSection.current === null)
        {
            return;
        }

        if (messageSection.current.scrollTop === 0)
        {
            onScrolledToTop();
        }
    }

    function handleScroll()
    {
        if (messageSection.current == null)
        {
            return;
        }
        scrolledToBottom.current = (messageSection.current.scrollHeight - messageSection.current.scrollTop - messageSection.current.clientHeight) < 10;
        previousDistFromBottom.current = messageSection.current.scrollHeight - messageSection.current.scrollTop;

        checkIfScrolledToTop();
    }

    function updateScrollPosition()
    {
        if (messageSection.current === null)
        {
            return;
        }
        // if was previously scrolled to the bottom:
        if (scrolledToBottom.current)
        {
            // scroll back to the bottom
            messageSection.current.scrollTop = messageSection.current.scrollHeight;
        }
        if (messageSection.current.children.length !== 0)
        {
            let currentFirstMessageID = parseInt(messageSection.current.children[0].getAttribute("id") as string);
            /* 
                if a new message was added to the top, i.e. if the first element is now different
                than the last saved first element
            */
            if (currentFirstMessageID != firstMessageID?.current)
            {
                messageSection.current.scrollTop = messageSection.current.scrollHeight - previousDistFromBottom.current;
            }
            firstMessageID.current = currentFirstMessageID;
        }
        previousDistFromBottom.current = messageSection.current.scrollHeight - messageSection.current.scrollTop;
    }


    useEffect(updateScrollPosition);

    return (
        <div 
            ref={messageSection} 
            className={classes.MessageSection} 
            onScroll={handleScroll}
            key={"messageSection"}
        >

            {
                messages.map
                (
                    (message) => <ChatMessage key={message.getID()} message={message}/>
                )
            }

        </div>
    )
}