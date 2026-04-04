//
// Created by rohitrai on 27/01/26.
//

#ifndef ASSISTANT_FULLDUPLEXPASS_H
#define ASSISTANT_FULLDUPLEXPASS_H

#include <oboe/Oboe.h>

using namespace oboe;
using namespace std;

class FullDuplexPass : public FullDuplexStream {
public:
    virtual DataCallbackResult onBothStreamsReady(
            const void *inputData,
            int numInputFrames,
            void *outputData,
            int numOutputFrames) {
        const float *inputFloats = static_cast<const float *>(inputData);
        float *outputFloats = static_cast<float *>(outputData);
        int32_t samplesPerFrame = getOutputStream()->getChannelCount();
        int32_t numInputSamples = numInputFrames * samplesPerFrame;
        int32_t numOutputSamples = numOutputFrames * samplesPerFrame;
        int32_t samplesToProcess = min(numInputSamples, numOutputSamples);

        for (int32_t i = 0; i < samplesToProcess; i++) {
            *outputFloats++ = *inputFloats++ * 0.5;
        }

        int32_t samplesLeft = numOutputSamples - numInputSamples;

        for (int32_t i = 0; i < samplesLeft; i++) {
            *outputFloats++ = 0.0;
        }

        return DataCallbackResult::Continue;
    }
};

#endif //ASSISTANT_FULLDUPLEXPASS_H
