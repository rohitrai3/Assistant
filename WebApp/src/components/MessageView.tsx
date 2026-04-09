import type { MessageViewProps } from "../utils/types";

export default function MessageView({ index, message }: MessageViewProps) {
  const isRight = index % 2 === 0;

  return (
    <div className={`${isRight ? "items-end" : ""} flex flex-col`}>
      <p className={`${isRight ? "rounded-l-2xl bg-purple-dark" : "rounded-r-2xl bg-gray-dark"}
      w-fit max-w-9/10 px-4 py-2`}>
        {message.content}
      </p>
      <p className="text-xs opacity-25 pt-2">
        {message.from}
      </p>
    </div>
  );
}

