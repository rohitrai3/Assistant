import {
  MessageBody,
  SubscribeMessage,
  WebSocketGateway,
  WebSocketServer,
} from '@nestjs/websockets';
import { Server } from 'socket.io';
import { getTranscription } from 'src/STT';

@WebSocketGateway({
  cors: {
    origin: '*',
  },
})
export class EventsGateway {
  @WebSocketServer()
  server: Server;

  @SubscribeMessage('transcribe')
  async transcribe(@MessageBody() data: Buffer): Promise<string> {
    const transcription = await getTranscription(new Float32Array(data.buffer));

    return transcription ? transcription : '';
  }
}
