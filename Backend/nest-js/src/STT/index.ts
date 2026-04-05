import {
  AutomaticSpeechRecognitionPipeline,
  env,
  pipeline,
} from '@huggingface/transformers';
import { join } from 'path/posix';

env.localModelPath = join(process.cwd(), 'src/STT/models/');
env.allowRemoteModels = false;
env.allowLocalModels = true;
env.useBrowserCache = false;
let pipe: AutomaticSpeechRecognitionPipeline | void | null = null;

if (env.backends.onnx.wasm)
  env.backends.onnx.wasm.wasmPaths = join(process.cwd(), 'src/STT/wasm/');

export async function loadModel() {
  console.info('Loading model...');

  pipe = await pipeline('automatic-speech-recognition', 'whisper-tiny-en', {
    dtype: {
      encoder_model: 'fp32',
      decoder_model_merged: 'q4',
    },
  })
    .catch((err) => console.error('Error loading model: ', err))
    .finally(() => console.info('Model loaded'));
}

export async function getTranscription(audio: Float32Array): Promise<string> {
  if (pipe) {
    const text = await pipe(audio)
      .then((res) => res.text)
      .catch((err) => console.error('Error transcribing: ', err))
      .finally(() => console.info('Transcription complete.'));

    return text ? text : '';
  }

  return '';
}

export function isModelLoaded(): boolean {
  return pipe ? true : false;
}
