import { Injectable } from '@nestjs/common';
import { isModelLoaded } from './STT';

@Injectable()
export class AppService {
  getHello(): string {
    return 'Hello World!';
  }

  getModelStatus(): string {
    return isModelLoaded() ? 'Ready' : 'Not ready';
  }
}
