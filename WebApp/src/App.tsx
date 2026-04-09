import { useRef, useState } from "react";
import { MicOff, MicOn, Spinner } from "./utils/icons";
import { io } from "socket.io-client";
import { read_audio } from "@huggingface/transformers";

function App() {
  const [recording, setRecording] = useState<boolean>(false);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [transcription, setTranscription] = useState<string>("Transcription");
  const streamRef = useRef<MediaStream | null>(null);
  const mediaRecorderRef = useRef<MediaRecorder | null>(null);
  const chunksRef = useRef<Blob[]>([]);
  const socket = io(import.meta.env.VITE_BACKEND_URL);
  socket.on("transcription", (res: string) => {
    console.log("res: ", res);
    setTranscription(res);
  });

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
    <div className="font-roboto flex justify-center items-center w-dvw h-dvh">
      {isLoading ? <div className="animate-spin">{Spinner()}</div> :
        <div>
          <button className="bg-gray hover:bg-purple p-2 rounded-full cursor-pointer" onClick={onClick}>
            {recording ? MicOff() : MicOn()}
          </button>
          <p>{transcription}</p>
        </div>}
    </div>
  );
}

export default App;

