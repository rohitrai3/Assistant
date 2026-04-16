import type { Socket } from "socket.io-client";
import UserMessageView from "./UserMessageView";
import AssistantThinkingView from "./AssistantThinkingView";
import { useEffect, useRef, useState } from "react";
import AssistantResponseView from "./AssistantResponseView";
import AssistantToolView from "./AssistantToolView";

type ConversationViewProps = {
  socket: Socket;
}

export default function ConversationView({
  socket,
}: ConversationViewProps) {
  const [userContents, setUserContents] = useState<string[]>([]);
  const [assistantThinkingContents, setAssistantThinkingContents] = useState<string[]>([]);
  const [assistantResponseContents, setAssistantResponseContents] = useState<string[]>([]);
  const [assistantToolNameContents, setAssistantToolNameContents] = useState<string[]>([]);
  const [assistantToolInputContents, setAssistantToolInputContents] = useState<string[]>([]);
  const [assistantThinking, setAssistantThinking] = useState<string>("");
  const [assistantResponse, setAssistantResponse] = useState<string>("");
  const [assistantToolName, setAssistantToolName] = useState<string>("");
  const [assistantToolInput, setAssistantToolInput] = useState<string>("");
  const bottomDivRef = useRef<HTMLDivElement | null>(null);
  const audioRef = useRef(new Audio());

  if (bottomDivRef.current) bottomDivRef.current.scrollIntoView();

  useEffect(() => {
    let thinking = "";
    let response = "";
    let toolName = "";
    let toolInput = "";

    socket.on("user.message", res => {
      setUserContents(prev => [...prev, res]);
    });

    socket.on("assistant.thinking.start", () => {
      thinking = "";
    });

    socket.on("assistant.thinking", res => {
      thinking = thinking + res;
      setAssistantThinking(prev => prev + res);
    });

    socket.on("assistant.response.start", () => {
      setAssistantThinkingContents(prev => [...prev, thinking]);
      setAssistantThinking("");
      response = "";
    });

    socket.on("assistant.response", (res) => {
      response = response + res;
      setAssistantResponse(prev => prev + res);
    });

    socket.on("assistant.signature", () => {
      setAssistantResponseContents(prev => [...prev, response]);
      setAssistantToolNameContents(prev => [...prev, toolName]);
      setAssistantToolInputContents(prev => [...prev, toolInput]);
      setAssistantResponse("");
      setAssistantToolName("");
      setAssistantToolInput("");
    });

    socket.on("assistant.tool.start", (res) => {
      setAssistantToolName(res);
      toolName = res;
      toolInput = "";
    });

    socket.on("assistant.tool", (res) => {
      setAssistantToolInput(res);
      toolInput = toolInput + res;
    });

    socket.on("speech", (res) => {
      const bufferData = new Uint8Array(res);
      const blob = new Blob([bufferData], { type: "audio/wav" });
      const objectURL = URL.createObjectURL(blob);
      audioRef.current.src = objectURL;
      audioRef.current.play();
    });

    return () => {
      socket.off("user.message", () => console.log("Closing user message event"));
      socket.off("assistant.thinking.start", () => console.log("Closing assistant thinking start event"));
      socket.off("assistant.thinking", () => console.log("Closing assistant thinking event"));
      socket.off("assistant.response.start", () => console.log("Closing assistant response start event"));
      socket.off("assistant.response", () => console.log("Closing assistant response event"));
      socket.off("assistant.signature", () => console.log("Closing assistant signature event"));
      socket.off("assistant.tool.start", () => console.log("Closing assistant tool start event"));
      socket.off("assistant.tool", () => console.log("Closing assistant tool event"));
      socket.off("speech", () => console.log("Closing speech event"));
    };
  }, []);

  return (
    <div className="w-full flex-1 flex flex-col gap-4 overflow-auto">
      <div className="mt-auto" />
      {userContents.map((content, index) => <div key={index} className="flex flex-col gap-4">
        <UserMessageView content={content} />
        {assistantThinkingContents[index] &&
          <AssistantThinkingView content={assistantThinkingContents[index]} />}
        {assistantToolNameContents[index] &&
          <AssistantToolView
            name={assistantToolNameContents[index]}
            input={assistantToolInputContents[index]} />}
        {assistantResponseContents[index] &&
          <AssistantResponseView content={assistantResponseContents[index]} />}
      </div>)}
      {assistantThinking && <AssistantThinkingView content={assistantThinking} />}
      {assistantToolName && <AssistantToolView name={assistantToolName} input={assistantToolInput} />}
      {assistantResponse && <AssistantResponseView content={assistantResponse} />}
      <div ref={bottomDivRef} />
      <audio className="hidden" ref={audioRef} controls />
    </div>
  );
}

