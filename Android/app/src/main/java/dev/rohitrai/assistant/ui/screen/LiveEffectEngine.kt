package dev.rohitrai.assistant.ui.screen

import android.content.Context
import android.media.AudioManager

class LiveEffectEngine {
    init {
        System.loadLibrary("Assistant")
    }

    external fun create(): Boolean
    external fun isAAudioRecommended(): Boolean
    external fun setEffectOn(isEffectOn: Boolean): Boolean
    external fun setAPI(apiType: Int): Boolean
    external fun setRecordingDeviceId(deviceId: Int)
    external fun setPlaybackDeviceId(deviceId: Int)
    external fun native_setDefaultStreamValues(defaultSampleRate: Int, defaultFramesPerBurst: Int)

    fun setDefaultStreamValues(context: Context) {
        val myAudioManger = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val sampleRate = myAudioManger.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE)
        val defaultSampleRate = sampleRate.toInt()
        val framesPerBurst = myAudioManger.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER)
        val defaultFramesPerBurst = framesPerBurst.toInt()

        native_setDefaultStreamValues(defaultSampleRate, defaultFramesPerBurst)
    }
}
