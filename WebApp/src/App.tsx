import { useState } from "react";
import AudioInput from "./components/AudioInput";
import { loadModel } from "./utils/Whisper";
import { Spinner } from "./utils/icons";

function App() {
  const [isLoading, setIsLoading] = useState<boolean>(true);
  loadModel(setIsLoading);

  return (
    <div className="font-roboto w-dvw h-dvh flex justify-center items-center">
      {isLoading ? <div className="animate-spin">{Spinner()}</div> : <AudioInput />}
    </div>
  );
}

export default App;

