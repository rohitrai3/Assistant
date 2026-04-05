import { NestFactory } from '@nestjs/core';
import { AppModule } from './app.module';
import { loadModel } from './STT';

async function bootstrap() {
  const app = await NestFactory.create(AppModule);
  app.enableCors();
  await app.listen(process.env.PORT ?? 3000);
  await loadModel();
}

bootstrap();
