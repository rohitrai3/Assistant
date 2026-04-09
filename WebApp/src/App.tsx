import { useState } from "react";
import { io } from "socket.io-client";
import type { Message } from "./utils/types";
import ConversationView from "./components/ConversationView";
import AudioInput from "./components/AudioInput";

function App() {
  const [messages, setMessages] = useState<Message[]>([]);
  const socket = io(import.meta.env.VITE_BACKEND_URL);

  return (
    <div className="font-roboto flex flex-col gap-4 justify-center items-center w-dvw h-dvh">
      <ConversationView messages={messages} setMessages={setMessages} socket={socket} />
      <AudioInput socket={socket} />
    </div>
  );
}

export default App;

