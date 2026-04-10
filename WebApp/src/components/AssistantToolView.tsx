type AssistantToolView = {
  name: string;
  input: string;
}

export default function AssistantToolView({ name, input }: AssistantToolView) {

  return (
    <div className="flex flex-col">
      <p className="text-xs opacity-25">
        Assistant: Tool...
      </p>
      <p className="text-sm text-gray border border-l-0 border-gray-dark font-gray rounded-r-2xl w-fit max-w-9/10 px-4 py-2">
        Name: {name}, Input: {input}
      </p>
    </div>
  );
}

