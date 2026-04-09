import { useState } from "react";
import { io } from "socket.io-client";
import type { Message } from "./utils/types";
import ConversationView from "./components/ConversationView";
import AudioInput from "./components/AudioInput";

function App() {
  const [messages, setMessages] = useState<Message[]>([]);
  const [isConnected, setIsConnected] = useState<boolean>(false);
  const socket = io(import.meta.env.VITE_BACKEND_URL);
  socket.on("connect", () => setIsConnected(true));
  socket.on("disconnect", () => setIsConnected(true));

  return (
    <div className="font-roboto flex flex-col gap-4 justify-center items-center w-dvw h-dvh">
      <ConversationView messages={messages} setMessages={setMessages} socket={socket} />
      <AudioInput isConnected={isConnected} socket={socket} />
    </div>
  );
}

export default App;

