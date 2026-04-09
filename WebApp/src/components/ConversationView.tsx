import type { ConversationViewProps } from "../utils/types";
import MessageView from "./MessageView";

export default function ConversationView({ messages }: ConversationViewProps) {

  return (
    <div className="w-full flex-1 flex flex-col justify-end gap-4">
      {messages.map((message, index) => <MessageView key={index} index={index} message={message} />)}
    </div>
  );
}

