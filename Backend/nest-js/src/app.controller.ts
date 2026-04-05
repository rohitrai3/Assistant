import { Body, Controller, Get, Post, Req } from '@nestjs/common';
import { AppService } from './app.service';

@Controller()
export class AppController {
  constructor(private readonly appService: AppService) {}

  @Get()
  getHello(): string {
    return this.appService.getHello();
  }

  @Get('model/status')
  getModelStatus(): string {
    return this.appService.getModelStatus();
  }

  @Post('transcribe')
  getTranscription(@Req() data) {
    console.log('Data: ', data);
  }
}
