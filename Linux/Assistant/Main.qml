import QtQuick
import QtQuick.Dialogs
import QtQuick.Controls.Basic
import Assistant
import QtMultimedia

Window {
    id: root
    width: 640
    height: 480
    visible: true

    CaptureSession {
        audioInput: AudioInput { id: audioInput }
        recorder: MediaRecorder{
            id: recorder
            outputLocation: "/run/media/rohitrai/Storage/Projects/Assistant/Linux/Assistant/audio_files/output.aac"
        }
        Component.onCompleted: {
            console.log("audioInput.device:")
            console.log(audioInput.device)
            recorder.record()
        }
    }
}
