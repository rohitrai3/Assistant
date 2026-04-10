import ConversationView from "./ConversationView";
import AudioInput from "./AudioInput";
import type { Socket } from "socket.io-client";

type HomepageProps = {
  socket: Socket;
}

export default function Homepage({ socket }: HomepageProps) {

  return (
    <div className="w-full h-full flex flex-col gap-4 justify-center items-center">
      <ConversationView socket={socket} />
      <AudioInput socket={socket} />
    </div>
  );
}

