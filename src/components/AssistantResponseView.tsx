type AssistantResponseView = {
  content: string;
}

export default function AssistantResponseView({ content }: AssistantResponseView) {

  return (
    <div className="flex flex-col">
      <p className="bg-gray-dark rounded-r-2xl w-fit max-w-9/10 px-4 py-2">
        {content}
      </p>
    </div>
  );
}

