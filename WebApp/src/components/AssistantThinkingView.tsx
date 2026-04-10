type AssistantThinkingView = {
  content: string;
}

export default function AssistantThinkingView({ content }: AssistantThinkingView) {

  return (
    <div className="flex flex-col">
      <p className="text-xs opacity-25">
        Assistant: Thinking...
      </p>
      <p className="text-sm text-gray border border-gray-dark font-gray rounded-r-2xl w-fit max-w-9/10 px-4 py-2">
        {content}
      </p>
    </div>
  );
}

