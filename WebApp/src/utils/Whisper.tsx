import { AutomaticSpeechRecognitionPipeline, env, pipeline } from "@huggingface/transformers";
import type { Dispatch, SetStateAction } from "react";

env.localModelPath = "/models/";
env.allowRemoteModels = false;
env.allowLocalModels = true;
env.useBrowserCache = false;
let pipe: AutomaticSpeechRecognitionPipeline | void | null = null;

if (env.backends.onnx.wasm) env.backends.onnx.wasm.wasmPaths = "/public/wasm/";

export async function loadModel(setIsLoading: Dispatch<SetStateAction<boolean>>) {
  pipe = await pipeline("automatic-speech-recognition", "whisper-tiny-en", {
    dtype: {
      encoder_model: "fp32",
      decoder_model_merged: "q4"
    }
  })
    .catch(err => console.error("Error loading model: ", err))
    .finally(() => setIsLoading(false));
}

export async function getTranscription(audio: string, setTranscription: Dispatch<SetStateAction<string>>, setIsLoading: Dispatch<SetStateAction<boolean>>) {
  if (pipe) {
    await pipe(audio)
      .then(res => setTranscription(res.text))
      .catch(err => console.error("Error transcribing: ", err))
      .finally(() => setIsLoading(false));
  } else {
    setIsLoading(false);
  }
}

