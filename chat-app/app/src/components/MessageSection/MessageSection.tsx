import { useEffect, useRef } from 'react';
import './MessageSection.css'

function MessageSection({messages, onScrolledToTop} : any)
{
    const messageSection = useRef<HTMLDivElement>(null);
    let previousScrollTop = useRef(0);
    let scrolledToBottom = useRef(true);


    function handleScroll(event: any)
    {
        if(messageSection.current == null)
        {
            return;
        }
        scrolledToBottom.current = (messageSection.current.scrollHeight - messageSection.current.scrollTop - messageSection.current.clientHeight) < 1;
        previousScrollTop.current = messageSection.current.scrollTop;
    }
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

    useEffect
    (
        function _()
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
                // go back to the previous scrollTop value (before new messages were added)
                messageSection.current.scrollTop = previousScrollTop.current;
            }
        }
    )
    setInterval
    (
        checkIfScrolledToTop,
        1000
    )
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