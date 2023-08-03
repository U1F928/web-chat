import './App.css'
import { Route, Routes } from 'react-router-dom'
import { JoinChatRoom } from './components/JoinChatRoom/JoinChatRoom'
import { ChatRoom } from './components/ChatRoom/ChatRoom'

function App() 
{
  return (
    <Routes>
      <Route path="/" element={<JoinChatRoom />} />
      <Route path="/:roomName" element={<ChatRoom />} />
    </Routes>
  )
}

export default App
