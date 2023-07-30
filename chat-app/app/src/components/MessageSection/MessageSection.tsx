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
        console.log("current dist from bottom:" + previousDistFromBottom.current);
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

    function updateScrollPosition()
    {
        console.log("UPDATING SCROLL POSITION!")
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
            console.log("prev dist from bottom:" + previousDistFromBottom.current);
            messageSection.current.scrollTop = messageSection.current.scrollHeight - previousDistFromBottom.current;
        }
    }

    setInterval
    (
        checkIfScrolledToTop,
        1000
    );

    useEffect(updateScrollPosition);

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

export default MessageSection;