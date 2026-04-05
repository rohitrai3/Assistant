import "dotenv/config";
import express from "express";
import { createServer } from "http";
import { Server } from "socket.io";
import { getTranscription, loadModel } from "./STT.ts";

const app = express();
const server = createServer(app);
const io = new Server(server, {
  cors: {
    origin: process.env.ORIGIN_URL
  }
});


server.listen(3000, async () => {
  await loadModel();
  console.log("Server listening at port 3000");
});

io.on("connection", (socket) => {
  console.log("a user connected");
  socket.on("transcribe", async (data) => {
    console.log("a new transcribe");
    const transcription = await getTranscription(new Float32Array(data.buffer));

    socket.emit("transcription", transcription);
  });
});

