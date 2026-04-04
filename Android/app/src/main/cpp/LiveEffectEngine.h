//
// Created by rohitrai on 26/01/26.
//

#ifndef ASSISTANT_LIVEEFFECTENGINE_H
#define ASSISTANT_LIVEEFFECTENGINE_H

#include <FullDuplexPass.h>
#include <oboe/Oboe.h>

using namespace oboe;
using namespace std;

class LiveEffectEngine : public AudioStreamCallback {
public:
    LiveEffectEngine();

    void setRecordingDeviceId(int32_t deviceId);
    void setPlaybackDeviceId(int32_t deviceId);
    void onErrorBeforeClose(AudioStream *oboeStream, Result error) override;
    void onErrorAfterClose(AudioStream *oboeStream, Result error) override;
    bool isAAudioRecommended(void);
    bool setEffectOn(bool isOn);
    bool setAudioApi(AudioApi);
    DataCallbackResult onAudioReady(
            AudioStream *oboeStream, void *audioData, int32_t numFrames) override;
    Result openRecording();
    Result startRecording();
    deque<float> stopRecording();
    vector<float> stopVectorRecording();
    Result startPlayback();
    Result stopPlayback();

private:
    static deque<float> mAudioData;
    static vector<float> mVectorAudioData;
    static const int32_t mInputChannelCount = ChannelCount::Mono;
    static const int32_t mOutputChannelCount = ChannelCount::Mono;
    const AudioFormat mFormat = AudioFormat::Float;
    bool mIsEffectOn = false;
    int32_t mRecordingDeviceId = kUnspecified;
    int32_t mPlaybackDeviceId = kUnspecified;
    int32_t mSampleRate = kUnspecified;
    AudioApi mAudioApi = AudioApi::AAudio;

    shared_ptr<AudioStream> mRecordingStream;
    shared_ptr<AudioStream> mPlaybackStream;
    shared_ptr<FullDuplexPass> mDuplexStream;

    void closeStream(shared_ptr<AudioStream> &stream);
    void closeStreams();
    void warnIfNotLowLatency(shared_ptr<AudioStream> &stream);
    Result openStreams();
    AudioStreamBuilder *setupCommonStreamParameters(
            AudioStreamBuilder *builder);
    AudioStreamBuilder *setupRecordingStreamParameters(
            AudioStreamBuilder *builder, int32_t sampleRate);
    AudioStreamBuilder *setupPlaybackStreamParameters(
            AudioStreamBuilder *builder);

    class MyDataCallback : public AudioStreamDataCallback {
    public:
        DataCallbackResult onAudioReady(
                AudioStream *audioStream,
                void *audioData,
                int32_t numFrames) override;

    };
    shared_ptr<MyDataCallback> mDataCallback;

    class MyPlaybackDataCallback : public AudioStreamDataCallback {
    public:
        DataCallbackResult onAudioReady(
                AudioStream *audioStream,
                void *audioData,
                int32_t numFrames) override;

    };
    shared_ptr<MyPlaybackDataCallback> mPlaybackDataCallback;
};

#endif //ASSISTANT_LIVEEFFECTENGINE_H
