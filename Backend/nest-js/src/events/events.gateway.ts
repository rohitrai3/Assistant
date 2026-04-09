import { Logger } from '@nestjs/common';
import {
  MessageBody,
  SubscribeMessage,
  WebSocketGateway,
  WebSocketServer,
} from '@nestjs/websockets';
import { config } from 'dotenv';
import { Server } from 'socket.io';
import McpClient from 'src/providers/MCPClient';
import SttModel from 'src/providers/STT';
import { Message } from 'src/utils/types';

config();

@WebSocketGateway({
  cors: {
    origin: [process.env.ORIGIN_URL],
  },
})
export class EventsGateway {
  private readonly logger = new Logger("EventsGateway");
  @WebSocketServer()
  server: Server;

  constructor(
    private sttModel: SttModel,
    private mcpClient: McpClient
  ) {
    this.sttModel.load();
    this.mcpClient.connectToServer(process.env.MCP_SERVER_PATH);
  }

  @SubscribeMessage("conversation")
  async conversation(@MessageBody() data: Buffer) {
    this.logger.log("Message received");
    const transcription = await this.sttModel.getTranscription(new Float32Array(data.buffer));
    const userMessage: Message = {
      from: "user",
      content: transcription
    };

    this.logger.log("Transcription send");
    this.server.emit("conversation", userMessage);

    const chat = await this.mcpClient.processQuery(transcription);
    const assistantMessage: Message = {
      from: "assistant",
      content: chat
    }

    this.logger.log("LLM reply sent");
    this.server.emit("conversation", assistantMessage);
  }

}

