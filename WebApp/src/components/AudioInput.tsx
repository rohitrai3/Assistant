import { useRef, useState } from "react";
import { MicOff, MicOn, Spinner } from "../utils/icons";
import { webmFixDuration } from "../utils/BlobFix";
import { getTranscription } from "../utils/Whisper";

function AudioInput() {
  const [recording, setRecording] = useState<boolean>(false);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [transcription, setTranscription] = useState<string>("Transcription");
  const streamRef = useRef<MediaStream | null>(null);
  const mediaRecorderRef = useRef<MediaRecorder | null>(null);
  const chunksRef = useRef<Blob[]>([]);

  function getMimeType() {
    const types = [
      "audio/webm",
      "audio/mp4",
      "audio/ogg",
      "audio/wav",
      "audio/aac",
    ];

    for (let i = 0; i < types.length; i++) {
      if (MediaRecorder.isTypeSupported(types[i])) {

        return types[i];
      }
    }

    return undefined;
  }

  async function startRecording() {
    const startTime = Date.now();
    const mimeType = getMimeType();
    streamRef.current = await navigator.mediaDevices.getUserMedia({ audio: true });
    const mediaRecorder = new MediaRecorder(streamRef.current, { mimeType });
    mediaRecorderRef.current = mediaRecorder;

    mediaRecorder.addEventListener("dataavailable", async (event) => {
      if (event.data.size > 0) {
        chunksRef.current.push(event.data);
      }

      if (mediaRecorder.state === "inactive") {
        const duration = Date.now() - startTime;
        let blob = new Blob(chunksRef.current, { type: mimeType });

        if (mimeType === "audio/webm") {
          blob = await webmFixDuration(blob, duration, blob.type);
        }

        chunksRef.current = [];
        setIsLoading(true);
        await getTranscription(URL.createObjectURL(blob), setTranscription, setIsLoading);
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

  return (<>
    {isLoading ? <div className="animate-spin">{Spinner()}</div> :
      <div>
        <button className="bg-gray hover:bg-purple p-2 rounded-full cursor-pointer" onClick={onClick}>
          {recording ? MicOff() : MicOn()}
        </button>
        <p>{transcription}</p>
      </div>}
  </>);
}

export default AudioInput;

