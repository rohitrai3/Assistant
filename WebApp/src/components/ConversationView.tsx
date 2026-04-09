import type { ConversationViewProps, Message } from "../utils/types";
import MessageView from "./MessageView";

export default function ConversationView({ messages, setMessages, socket }: ConversationViewProps) {
  socket.on("conversation", (res: Message) => {
    setMessages([...messages, res]);
  });


  return (
    <div className="w-full flex-1 flex flex-col justify-end gap-4">
      {messages.map((message, index) => <MessageView key={index} index={index} message={message} />)}
    </div>
  );
}

