import { useRef, useState } from "react";
import { MicOff, MicOn } from "../utils/icons";
import { read_audio } from "@huggingface/transformers";
import type { Socket } from "socket.io-client";

type AudioInputProps = {
  socket: Socket;
}

export default function AudioInput({
  socket,
}: AudioInputProps) {
  const [recording, setRecording] = useState<boolean>(false);
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

        const audioData = await read_audio(URL.createObjectURL(blob), 16000);

        socket.emit("conversation", audioData);

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
    <div className="mb-4">
      <button
        className="p-2 bg-gray cursor-pointer hover:bg-purplep-2 rounded-full"
        onClick={onClick}
      >
        {recording ? MicOff() : MicOn()}
      </button>
    </div >
  );
}

