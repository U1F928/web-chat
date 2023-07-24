import './App.css'
import { Route, Routes } from 'react-router-dom'

import JoinRoom from './components/JoinRoom/JoinRoom'
import Chat from './components/Chat/Chat'

function App () 
{
  return (
      <Routes>
        <Route path="/" element={<JoinRoom />}/> 
        <Route path="/:roomName" element={<Chat />}/> 
      </Routes>
  )
}

export default App
