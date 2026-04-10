import { io } from "socket.io-client";
import Homepage from "./components/Homepage";

function App() {
  const socket = io(import.meta.env.VITE_BACKEND_URL);

  return (
    <div className="font-roboto w-screen h-screen">
      <Homepage socket={socket} />
    </div>
  );
}

export default App;

