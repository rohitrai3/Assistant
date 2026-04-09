import type { MessageViewProps } from "../utils/types";

export default function MessageView({ index, message }: MessageViewProps) {
  const isRight = index % 2 === 0;

  return (
    <div className={`${isRight ? "text-right" : ""}`}>
      <p>
        <span className={`px-4 py-2 ${isRight ? "rounded-l-full bg-purple-dark" : "rounded-r-full bg-gray-dark"}`}>
          {message.content}
        </span>
      </p>
      <p className="text-xs opacity-25 pt-2">
        {message.from}
      </p>
    </div>
  );
}

