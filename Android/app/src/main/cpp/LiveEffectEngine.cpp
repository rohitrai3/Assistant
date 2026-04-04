//
// Created by rohitrai on 26/01/26.
//

#include <android/log_macros.h>
#include <cassert>
#include <LiveEffectEngine.h>

#define LOG_TAG "LiveEffectEngine"

using namespace oboe;
using namespace std;
deque<float> LiveEffectEngine::mAudioData;
vector<float> LiveEffectEngine::mVectorAudioData;

LiveEffectEngine::LiveEffectEngine() {
    assert(mOutputChannelCount == mInputChannelCount);
}

void LiveEffectEngine::setRecordingDeviceId(int32_t deviceId) {
    mRecordingDeviceId = deviceId;
}

void LiveEffectEngine::setPlaybackDeviceId(int32_t deviceId) {
    mPlaybackDeviceId = deviceId;
}

void LiveEffectEngine::warnIfNotLowLatency(shared_ptr<AudioStream> &stream) {
    if (stream->getPerformanceMode() != PerformanceMode::LowLatency) {
        ALOGW("%s() : Stream is NOT low latency. "
              "Check your requested format, sample rate and channel count", __func__);
    }
}

void LiveEffectEngine::onErrorBeforeClose(AudioStream *oboeStream, Result error) {
    ALOGE("%s() : %s stream Error before close: %s",
          __func__, convertToText(oboeStream->getDirection()), convertToText(error));
}

void LiveEffectEngine::onErrorAfterClose(AudioStream *oboeStream, Result error) {
    ALOGE("%s() : %s stream Error after close: %s",
          __func__, convertToText(oboeStream->getDirection()), convertToText(error));
    closeStreams();

    if(error == Result::ErrorDisconnected) {
        ALOGI("%s() : Restarting AudioStream.", __func__);
        openStreams();
    }
}

bool LiveEffectEngine::isAAudioRecommended(void) {
    return AudioStreamBuilder::isAAudioRecommended();
}

bool LiveEffectEngine::setEffectOn(bool isOn) {
    bool success = true;

    if (isOn != mIsEffectOn) {
        if (isOn) {
            success = openStreams() == Result::OK;

            if (success) {
                mIsEffectOn = isOn;
            }
        } else {
            closeStreams();
            mIsEffectOn = isOn;
        }
    }

    return success;
}

bool LiveEffectEngine::setAudioApi(AudioApi api) {
    if (mIsEffectOn) return false;

    mAudioApi = api;

    return true;
}

DataCallbackResult
LiveEffectEngine::onAudioReady(AudioStream *oboeStream, void *audioData, int32_t numFrames) {

    return mDuplexStream->onAudioReady(oboeStream, audioData, numFrames);
}

void LiveEffectEngine::closeStream(shared_ptr<AudioStream> &stream) {
    if (stream) {
        Result result = stream->stop();

        if (result != Result::OK) {
            ALOGW("%s() : Error stopping stream: %s", __func__, convertToText(result));
        }

        result = stream->close();

        if (result != Result::OK) {
            ALOGE("%s() : Error closing stream: %s", __func__, convertToText(result));
        } else {
            ALOGW("%s() : Successfully closed streams.", __func__);
        }

        stream.reset();
    }
}

void LiveEffectEngine::closeStreams() {
    mDuplexStream->stop();
    closeStream(mPlaybackStream);
    closeStream(mRecordingStream);
    mDuplexStream.reset();
}

Result LiveEffectEngine::openStreams() {
    AudioStreamBuilder inBuilder, outBuilder;
    setupPlaybackStreamParameters(&outBuilder);
    Result result = outBuilder.openStream(mPlaybackStream);

    if (result != Result::OK) {
        ALOGE("%s() : Failed to open output stream. Error %s", __func__, convertToText(result));
        mSampleRate = kUnspecified;

        return result;
    } else {
        mSampleRate = mPlaybackStream->getSampleRate();
    }

    warnIfNotLowLatency(mPlaybackStream);
    setupRecordingStreamParameters(&inBuilder, mSampleRate);
    result = inBuilder.openStream(mRecordingStream);
    if (result != Result::OK) {
        ALOGE("%s() : Failed to open input stream. Error %s", __func__, convertToText(result));
        closeStream(mPlaybackStream);

        return result;
    }

    warnIfNotLowLatency(mRecordingStream);

    mDuplexStream = make_unique<FullDuplexPass>();
    mDuplexStream->setSharedInputStream(mRecordingStream);
    mDuplexStream->setSharedOutputStream(mPlaybackStream);
    mDuplexStream->start();

    return result;
}

AudioStreamBuilder *LiveEffectEngine::setupCommonStreamParameters(AudioStreamBuilder *builder) {
    builder->setAudioApi(mAudioApi)
        ->setFormat(mFormat)
        ->setFormatConversionAllowed(true)
        ->setSharingMode(SharingMode::Exclusive)
        ->setPerformanceMode(PerformanceMode::LowLatency);

    return builder;
}

AudioStreamBuilder *
LiveEffectEngine::setupRecordingStreamParameters(AudioStreamBuilder *builder, int32_t sampleRate) {
    mDataCallback = std::make_shared<MyDataCallback>();

    builder->setDataCallback(mDataCallback)
        ->setDeviceId(mRecordingDeviceId)
        ->setDirection(Direction::Input)
        ->setSampleRate(sampleRate)
        ->setChannelCount(mInputChannelCount);
    return setupCommonStreamParameters(builder);
}

AudioStreamBuilder *LiveEffectEngine::setupPlaybackStreamParameters(AudioStreamBuilder *builder) {
    mPlaybackDataCallback = std::make_shared<MyPlaybackDataCallback>();

    builder->setDataCallback(mPlaybackDataCallback)
        ->setErrorCallback(this)
        ->setDeviceId(mPlaybackDeviceId)
        ->setDirection(Direction::Output)
        ->setChannelCount(mOutputChannelCount);

    return setupCommonStreamParameters(builder);
}

Result LiveEffectEngine::openRecording() {
    ALOGI("%s() : Open recording stream.", __func__);

    AudioStreamBuilder inBuilder, outBuilder;
    setupPlaybackStreamParameters(&outBuilder);
    Result result = outBuilder.openStream(mPlaybackStream);

    if (result != Result::OK) {
        ALOGE("%s() : Failed to open output stream. Error %s", __func__, convertToText(result));
        mSampleRate = kUnspecified;

        return result;
    } else {
        mSampleRate = mPlaybackStream->getSampleRate();
    }

    warnIfNotLowLatency(mPlaybackStream);
    setupRecordingStreamParameters(&inBuilder, mSampleRate);
    result = inBuilder.openStream(mRecordingStream);
    if (result != Result::OK) {
        ALOGE("%s() : Failed to open input stream. Error %s", __func__, convertToText(result));
        closeStream(mPlaybackStream);

        return result;
    }

    warnIfNotLowLatency(mRecordingStream);

    return result;
}

Result LiveEffectEngine::startRecording() {
    ALOGI("%s() : Start recording stream.", __func__);
    ALOGI("%s() : Audio data size: %d", __func__, mAudioData.size());

    return mRecordingStream->requestStart();
}

deque<float> LiveEffectEngine::stopRecording() {
    ALOGI("%s() : Stop recording stream.", __func__);
    ALOGI("%s() : Audio data size: %d", __func__, mAudioData.size());

    mRecordingStream->requestStop();

    return mAudioData;
}

vector<float> LiveEffectEngine::stopVectorRecording() {
    ALOGI("%s() : Stop vector recording stream.", __func__);
    ALOGI("%s() : Vector audio data size: %d", __func__, mAudioData.size());

    mRecordingStream->requestStop();

    return mVectorAudioData;
}

Result LiveEffectEngine::startPlayback() {
    ALOGI("%s() : Start playback stream.", __func__);
    ALOGI("%s() : Audio data size: %d", __func__, mAudioData.size());

    return mPlaybackStream->requestStart();
}

Result LiveEffectEngine::stopPlayback() {
    ALOGI("%s() : Stop playback stream.", __func__);
    ALOGI("%s() : Audio data size: %d", __func__, mAudioData.size());

    return mPlaybackStream->requestStop();
}

DataCallbackResult
LiveEffectEngine::MyDataCallback::onAudioReady(AudioStream *audioStream, void *audioData,
                                               int32_t numFrames) {
    const float *inputFloats = static_cast<const float *>(audioData);
    int32_t samplesPerFrame = mInputChannelCount;
    int32_t numInputSamples = numFrames * samplesPerFrame;

    for (int32_t i = 0; i < numInputSamples; i++) {
        float val = *inputFloats++;
        mAudioData.push_back(val);
        mVectorAudioData.push_back(val);
    }

    return DataCallbackResult::Continue;
}

DataCallbackResult
LiveEffectEngine::MyPlaybackDataCallback::onAudioReady(AudioStream *audioStream, void *audioData,
                                                       int32_t numFrames) {
    float *output = (float *) audioData;

    int numOutputSamples = numFrames * mOutputChannelCount;

    for (int i = 0; i < numOutputSamples; i++) {
        if (!mAudioData.empty()) {
            *output++ = mAudioData.front();
            mAudioData.pop_front();
        }
    }

    return DataCallbackResult::Continue;
}
