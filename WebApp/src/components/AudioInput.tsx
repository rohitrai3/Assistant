import { useRef, useState } from "react";
import { MicOff, MicOn, Spinner } from "../utils/icons";
import { read_audio } from "@huggingface/transformers";
import type { AudioInputProps } from "../utils/types";

export default function AudioInput({ socket }: AudioInputProps) {
  const [recording, setRecording] = useState<boolean>(false);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const streamRef = useRef<MediaStream | null>(null);
  const mediaRecorderRef = useRef<MediaRecorder | null>(null);
  const chunksRef = useRef<Blob[]>([]);

  async function startRecording() {
    streamRef.current = await navigator.mediaDevices.getUserMedia({ audio: true });
    const mediaRecorder = new MediaRecorder(streamRef.current, { mimeType: "audio/webm" });
    mediaRecorderRef.current = mediaRecorder;

    mediaRecorder.addEventListener("dataavailable", async (event) => {
      if (event.data.size > 0) {
        chunksRef.current.push(event.data);
      }

      if (mediaRecorder.state === "inactive") {
        let blob = new Blob(chunksRef.current, { type: "audio/wav" });

        chunksRef.current = [];
        setIsLoading(true);

        const audioData = await read_audio(URL.createObjectURL(blob), 16000);
        socket.emit("transcribe", audioData);


        setIsLoading(false);

        streamRef.current?.getTracks().forEach(track => track.stop());
      }
    });

    mediaRecorder.start();
    setRecording(true);
  }

  function stopRecording() {
    if (mediaRecorderRef.current && mediaRecorderRef.current.state === "recording") {
      mediaRecorderRef.current.stop();
      setRecording(false);
    }
  };


  function onClick() {
    if (recording) {
      stopRecording();
    } else {
      startRecording();
    }
  }

  return (
    <div>
      {
        isLoading ?
          <div className="animate-spin">{Spinner()}</div> :
          <div className="mb-4">
            <button className="bg-gray hover:bg-purple p-2 rounded-full cursor-pointer" onClick={onClick}>
              {recording ? MicOff() : MicOn()}
            </button>
          </div>
      }
    </div>
  );
}

