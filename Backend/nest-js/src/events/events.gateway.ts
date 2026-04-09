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

  @SubscribeMessage('transcribe')
  async transcribe(@MessageBody() data: Buffer) {
    const transcription = await this.sttModel.getTranscription(new Float32Array(data.buffer));

    this.server.emit("transcription", transcription);

    // const chat = await this.mcpClient.processQuery(this.transcription);
    // this.logger.log("chat: ", chat);
  }

}

