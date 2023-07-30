import { useEffect, useRef } from 'react';
import './MessageSection.css'

function MessageSection({messages, onScrolledToTop} : any)
{
    const messageSection = useRef<HTMLDivElement>(null);
    const previousDistFromBottom = useRef(0);
    const scrolledToBottom = useRef(true);

    function handleScroll()
    {
        if(messageSection.current == null)
        {
            return;
        }
        scrolledToBottom.current = (messageSection.current.scrollHeight - messageSection.current.scrollTop - messageSection.current.clientHeight) < 1;
        previousDistFromBottom.current = messageSection.current.scrollHeight - messageSection.current.scrollTop;
    }

    useEffect
    (
        function updateScrollPosition()
        {
            if(messageSection.current === null)
            {
                return;
            }
            // if was previously scrolled to the bottom:
            if(scrolledToBottom.current)
            {
                // scroll back to the bottom
                messageSection.current.scrollTop = messageSection.current.scrollHeight;
            }
            else
            {
                messageSection.current.scrollTop = messageSection.current.scrollHeight - previousDistFromBottom.current;

            }
        }
    )

    function checkIfScrolledToTop()
    {
        if(messageSection.current === null)
        {
            return;
        }

        if(messageSection.current.scrollTop === 0)
        {
            onScrolledToTop();
        }
    }

    setInterval
    (
        checkIfScrolledToTop,
        1000
    );

    return (
    <div ref={messageSection} id="message-section" onScroll={handleScroll}>
        {messages}
        {/*
            <img src="/static/chat/loading_icon.svg" alt="Loading..." id="loading-icon">
            </img>
        */}
    </div>
   )
}

export default MessageSection