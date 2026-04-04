//
// Created by rohitrai on 26/01/26.
//
#include <jni.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>
#include <android/asset_manager.h>
#include <android/asset_manager_jni.h>
#include <android/log_macros.h>
#include <SimpleNoiseMaker.h>
#include <LiveEffectEngine.h>
#include <whisper.h>

#define LOG_TAG "LiveEffectEngine"
#define UNUSED(x) (void)(x)

using namespace oboe;

static const int kOboeApiAAudio = 0;
static const int kOboeApiOpenSLES = 1;
static LiveEffectEngine *engine = nullptr;


static size_t asset_read(void *ctx, void *output, size_t read_size) {
    return AAsset_read((AAsset *) ctx, output, read_size);
}

static bool asset_is_eof(void *ctx) {
    return AAsset_getRemainingLength64((AAsset *) ctx) <= 0;
}

static void asset_close(void *ctx) {
    AAsset_close((AAsset *) ctx);
}

static struct whisper_context *whisper_init_from_asset(
        JNIEnv *env, jobject assetManager, const char *asset_path) {
    ALOGI("%s() : Loading model form asset '%s'\n", __func__, asset_path);
    AAssetManager *asset_manager = AAssetManager_fromJava(env, assetManager);
    AAsset *asset = AAssetManager_open(asset_manager, asset_path, AASSET_MODE_STREAMING);

    if (!asset) {
        ALOGW("%s() : Failed to open '%s'\n", __func__, asset_path);

        return NULL;
    }

    whisper_model_loader loader = {
            .context = asset,
            .read = &asset_read,
            .eof = &asset_is_eof,
            .close = &asset_close
    };

    return whisper_init_with_params(&loader, whisper_context_default_params());
}

// JNI functions are "C" calling convention
#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jboolean JNICALL
Java_dev_rohitrai_assistant_ui_screen_LiveEffectEngine_create(JNIEnv *env, jclass) {
    if (engine == nullptr) {
        engine = new LiveEffectEngine();
    }

    return (engine != nullptr) ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT void JNICALL
Java_dev_rohitrai_assistant_ui_screen_LiveEffectEngine_delete(JNIEnv *env, jclass) {
    if (engine) {
        engine->setEffectOn(false);
        delete engine;
        engine = nullptr;
    }
}

JNIEXPORT jboolean JNICALL
Java_dev_rohitrai_assistant_ui_screen_LiveEffectEngine_setEffectOn(
        JNIEnv *env, jclass, jboolean isEffectOn) {
    if (engine == nullptr) {
        ALOGE("%s() : Engine is null, you must call createEngine before calling this "
              "method", __func__);

        return JNI_FALSE;
    }

    return engine->setEffectOn(isEffectOn) ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT void JNICALL
Java_dev_rohitrai_assistant_ui_screen_LiveEffectEngine_setRecordingDeviceId(
        JNIEnv *env, jclass, jint deviceId) {
    if (engine == nullptr) {
        ALOGE("%s() : Engine is null, you must call createEngine before calling this "
              "method", __func__);

        return;
    }

    engine->setRecordingDeviceId(deviceId);
}

JNIEXPORT void JNICALL
Java_dev_rohitrai_assistant_ui_screen_LiveEffectEngine_setPlaybackDeviceId(
        JNIEnv *env, jclass type, jint deviceId) {
    if (engine == nullptr) {
        ALOGE("%s() : Engine is null, you must call createEngine before calling this "
              "method", __func__);

        return;
    }

    engine->setPlaybackDeviceId(deviceId);
}

JNIEXPORT jboolean JNICALL
Java_dev_rohitrai_assistant_ui_screen_LiveEffectEngine_setAPI(
        JNIEnv *env, jclass type, jint apiType) {
    if (engine == nullptr) {
        ALOGE("%s() : Engine is null, you must call createEngine before calling this "
              "method", __func__);

        return JNI_FALSE;
    }

    AudioApi audioApi;
    switch (apiType) {
        case kOboeApiAAudio:
            audioApi = AudioApi::AAudio;
            break;
        case kOboeApiOpenSLES:
            audioApi = AudioApi::OpenSLES;
            break;
        default:
            ALOGE("%s() : Unknown API selection to setAPI() %d", __func__, apiType);
            return JNI_FALSE;
    }

    return engine->setAudioApi(audioApi) ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jboolean JNICALL
Java_dev_rohitrai_assistant_ui_screen_LiveEffectEngine_isAAudioRecommended(
        JNIEnv *env, jclass type) {
    if (engine == nullptr) {
        ALOGE("%s() : Engine is null, you must call createEngine before calling this "
              "method", __func__);

        return JNI_FALSE;
    }

    return engine->isAAudioRecommended() ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT void JNICALL
Java_dev_rohitrai_assistant_ui_screen_LiveEffectEngine_native_1setDefaultStreamValues(
        JNIEnv *env, jclass type, jint sampleRate, jint framesPerBurst) {
    DefaultStreamValues::SampleRate = (int32_t) sampleRate;
    DefaultStreamValues::FramesPerBurst = (int32_t) framesPerBurst;
}

JNIEXPORT jlong JNICALL
Java_dev_rohitrai_assistant_ui_screen_MainScreenViewModel_initContextFromAsset(
        JNIEnv *env, jobject thiz, jobject assetManager, jstring asset_path_str) {
    UNUSED(thiz);
    struct whisper_context *context = NULL;
    const char *asset_path_chars = (*env).GetStringUTFChars(asset_path_str, NULL);
    context = whisper_init_from_asset(env, assetManager, asset_path_chars);
    (*env).ReleaseStringUTFChars(asset_path_str, asset_path_chars);

    return (jlong) context;
}

JNIEXPORT jstring JNICALL
Java_dev_rohitrai_assistant_ui_screen_MainScreenViewModel_getSystemInfo(
        JNIEnv *env, jobject thiz) {
    UNUSED(thiz);
    const char *sysinfo = whisper_print_system_info();
    jstring string = (*env).NewStringUTF(sysinfo);

    return string;
}

JNIEXPORT void JNICALL
Java_dev_rohitrai_assistant_ui_screen_MainScreenViewModel_fullTranscribe(
        JNIEnv *env, jobject thiz, jlong context_ptr, jint num_threads, jfloatArray audio_data) {
    UNUSED(thiz);
    struct whisper_context *context = (struct whisper_context *) context_ptr;
    jfloat *audio_data_arr = (*env).GetFloatArrayElements(audio_data, NULL);
    const jsize audio_data_length = (*env).GetArrayLength(audio_data);
    ALOGI("%s() : audio_data_length: %d", __func__, audio_data_length);

    struct whisper_full_params params = whisper_full_default_params(WHISPER_SAMPLING_GREEDY);
    params.print_realtime = true;
    params.print_progress = false;
    params.print_timestamps = true;
    params.print_special = false;
    params.translate = false;
    params.language = "en";
    params.n_threads = num_threads;
    params.offset_ms = 0;
    params.no_context = true;
    params.single_segment = false;

    whisper_reset_timings(context);

    ALOGI("%s() : About to run whisper_full.", __func__);

    if (whisper_full(context, params, audio_data_arr, audio_data_length) != 0) {
        ALOGI("%s() : Failed to run the model.", __func__);
    } else {
        whisper_print_timings(context);
    }

    (*env).ReleaseFloatArrayElements(audio_data, audio_data_arr, JNI_ABORT);
}

JNIEXPORT jint JNICALL
Java_dev_rohitrai_assistant_ui_screen_MainScreenViewModel_startRecordingStream(
        JNIEnv * /*env*/, jobject) {
    ALOGI("%s() : Opening recording stream...", __func__);
    Result result = engine->openRecording();
    if (result == Result::OK) {
        ALOGI("%s() : Recording stream opened.", __func__);
        ALOGI("%s() : Starting recording stream...", __func__);
        result = engine->startRecording();
    }
    ALOGI("%s() : Recording stream started.", __func__);
    return (jint) result;
}

JNIEXPORT jfloatArray JNICALL
Java_dev_rohitrai_assistant_ui_screen_MainScreenViewModel_stopRecordingStream(
        JNIEnv *env, jobject) {
    ALOGI("%s() : Recording stopped.", __func__);
    deque<float> audioData = engine->stopRecording();
    vector<float> vectordata;
    for (float val : audioData) vectordata.push_back(val);
    jfloatArray result = (*env).NewFloatArray(audioData.size());
    (*env).SetFloatArrayRegion(result, 0, audioData.size(), vectordata.data());
    return result;
}

JNIEXPORT jint JNICALL
Java_dev_rohitrai_assistant_ui_screen_MainScreenViewModel_getTextSegmentCount(
        JNIEnv *env, jobject thiz, jlong context_ptr) {
    UNUSED(env);
    UNUSED(thiz);

    struct whisper_context *context = (struct whisper_context *) context_ptr;

    return whisper_full_n_segments(context);
}

JNIEXPORT jstring JNICALL
Java_dev_rohitrai_assistant_ui_screen_MainScreenViewModel_getTextSegment(
        JNIEnv *env, jobject thiz, jlong context_ptr, jint index) {
    UNUSED(thiz);

    struct whisper_context *context = (struct whisper_context *) context_ptr;
    const char *text = whisper_full_get_segment_text(context, index);
    jstring string = (*env).NewStringUTF(text);

    return string;
}

JNIEXPORT jint JNICALL
Java_dev_rohitrai_assistant_ui_screen_MainScreenViewModel_startPlaybackStream(
        JNIEnv * /*env*/, jobject) {
    ALOGI("%s() : Starting playback stream...", __func__);
    Result result = engine->startPlayback();
    ALOGI("%s() : Playback stream started.", __func__);
    return (jint) result;
}

JNIEXPORT jint JNICALL
Java_dev_rohitrai_assistant_ui_screen_MainScreenViewModel_stopPlaybackStream(
        JNIEnv *env, jobject) {
    ALOGI("%s() : Stopping playback stream...", __func__);
    Result result = engine->startPlayback();
    ALOGI("%s() : Playback stream stopped.", __func__);
    return (jint) result;
}

JNIEXPORT void JNICALL
Java_dev_rohitrai_assistant_ui_screen_MainScreenViewModel_stopRecordingAndTranscribe(
        JNIEnv *env, jobject thiz, jlong context_ptr, jint num_threads) {
    UNUSED(thiz);
    ALOGI("%s() : Recording stopped.", __func__);
    vector<float> audioData = engine->stopVectorRecording();
    struct whisper_context *context = (struct whisper_context *) context_ptr;
    ALOGI("%s() : audioData: %d", __func__, audioData.size());
//    jfloat *audio_data_arr = (*env).GetFloatArrayElements(audio_data, NULL);
    float *audio_data_arr = audioData.data();

    struct whisper_full_params params = whisper_full_default_params(WHISPER_SAMPLING_GREEDY);
    params.print_realtime = true;
    params.print_progress = false;
    params.print_timestamps = true;
    params.print_special = false;
    params.translate = false;
    params.language = "en";
    params.n_threads = num_threads;
    params.offset_ms = 0;
    params.no_context = true;
    params.single_segment = false;

    whisper_reset_timings(context);

    ALOGI("%s() : About to run whisper_full.", __func__);

    if (whisper_full(context, params, audio_data_arr, audioData.size()) != 0) {
        ALOGI("%s() : Failed to run the model.", __func__);
    } else {
        whisper_print_timings(context);
    }
}

#ifdef __cplusplus
}
#endif
