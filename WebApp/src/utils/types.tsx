import type { Dispatch, SetStateAction } from "react";
import type { Socket } from "socket.io-client";

export type AudioInputProps = {
  socket: Socket;
}

export type ConversationViewProps = {
  messages: Message[];
  setMessages: Dispatch<SetStateAction<Message[]>>;
  socket: Socket;
}

export type MessageViewProps = {
  index: number;
  message: Message;
}

export type Message = {
  from: string;
  content: string;
};

