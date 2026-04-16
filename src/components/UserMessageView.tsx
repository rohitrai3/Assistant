export type UserMessageViewProps = {
  content: string;
}

export default function UserMessageView({ content }: UserMessageViewProps) {

  return (
    <div className="items-end flex flex-col">
      <p className="text-xs opacity-25">
        User
      </p>
      <p className="rounded-l-2xl bg-purple-dark w-fit max-w-9/10 px-4 py-2">
        {content}
      </p>
    </div>
  );
}

