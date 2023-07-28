import './MessageSection.css'

function MessageSection({messages, onScrollToTop} : any)
{
    function handleScroll(event: any)
    {
        if(event.target.scrollTop === 0)
        {
            onScrollToTop();
        }
    }
    return (
    <div id="message-section" onScroll={handleScroll}>
        {messages}
        {/*
            <img src="/static/chat/loading_icon.svg" alt="Loading..." id="loading-icon">
            </img>
        */}
    </div>
   )
}

export default MessageSection