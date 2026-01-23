package dev.rohitrai.assistant

import android.annotation.SuppressLint
import android.content.res.AssetManager
import android.os.Build
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.Executors

class WhisperContext private constructor(private var ptr: Long) {
    private val TAG = "LibWhisper"

    private val scope: CoroutineScope = CoroutineScope(
        Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    )

    suspend fun transcribeData(data: FloatArray, printTimestamp: Boolean = true): String =
        withContext(scope.coroutineContext) {
            require(ptr != 0L)
            val numThreads = WhisperCpuConfig.preferredThreadCount
            Log.d(TAG, "L23: Selecting $numThreads threads.")
            WhisperLib.fullTranscribe(ptr, numThreads, data)

            val textCount = WhisperLib.getTextSegmentCount(ptr)
            return@withContext buildString {
                for (i in 0 until textCount) {
                    if (printTimestamp) {
                        val textTimestamp = "[${toTimestamp(WhisperLib.getTextSegmentT0(ptr, i))} --> ${toTimestamp(
                            WhisperLib.getTextSegmentT1(ptr, i))}]"
                        val textSegment = WhisperLib.getTextSegment(ptr, i)
                        append("$textTimestamp: $textSegment\n")
                    } else {
                        append(WhisperLib.getTextSegment(ptr, i))
                    }
                }
            }
        }
    suspend fun benchMemory(nthreads: Int): String = withContext(scope.coroutineContext) {
        return@withContext WhisperLib.benchMemcpy(nthreads)
    }

    suspend fun benchGgmlMulMat(nthreads: Int): String = withContext(scope.coroutineContext) {
        return@withContext WhisperLib.benchGgmlMulMat(nthreads)
    }

    companion object {
        fun createContextFromAsset(assetManager: AssetManager, assetPath: String): WhisperContext {
            val ptr = WhisperLib.initContextFromAsset(assetManager, assetPath)

            if (ptr == 0L) {
                throw RuntimeException("Couldn't create context from input stream")
            }
            return WhisperContext(ptr)
        }
        fun getSystemInfo(): String {
            return WhisperLib.getSystemInfo()
        }
    }
}

private class WhisperLib {


    companion object {
        private val TAG = "LibWhisper"
        init {
            Log.d(TAG, "L68: Primary ABI: ${Build.SUPPORTED_ABIS[0]}")
            var loadVfpv4 = false
            var loadV8fp16 = false
            if (isArmEabiV7a()) {
                val cpuInfo = cpuInfo()
                cpuInfo?.let {
                    Log.d(TAG, "CPU info: $cpuInfo")
                    if (cpuInfo.contains("vfpv4")) {
                        Log.d(TAG, "CPU supports vfpv4")
                        loadVfpv4 = true
                    }
                }
            } else if(isArmEabiV8a()) {
                val cpuInfo = cpuInfo()
                cpuInfo?.let {
                    Log.d(TAG, "L83: CPU info: $cpuInfo")
                    if (cpuInfo.contains("fphp")) {
                        Log.d(TAG, "CPU supports fp16 arithmetic")
                        loadV8fp16 = true
                    }
                }
            }

            if(loadVfpv4) {
                Log.d(TAG, "Loading libwhisper_vfpv4.so")
                System.loadLibrary("whisper_vfpv4")
            } else if (loadV8fp16) {
                Log.d(TAG, "Loading libwhisper_v8fp16_vs.so")
                System.loadLibrary("whisper_v8fp16_va")
            } else {
                Log.d(TAG, "L98: Loading assistant.so")
                System.loadLibrary("assistant")
            }
        }

        external fun initContextFromAsset(assetManager: AssetManager, assetPath: String): Long
        external fun fullTranscribe(contextPtr: Long, numThreads: Int, audioData: FloatArray)
        external fun getTextSegmentCount(contextPtr: Long): Int
        external fun getTextSegment(contextPtr: Long, index: Int): String
        external fun getTextSegmentT0(contextPtr: Long, index: Int): Long
        external fun getTextSegmentT1(contextPtr: Long, index: Int): Long
        external fun getSystemInfo(): String
        external fun benchMemcpy(nthread: Int): String
        external fun benchGgmlMulMat(nthread: Int): String
    }
}

@SuppressLint("DefaultLocale")
private fun toTimestamp(t: Long, comma: Boolean = false): String {
    var msec = t * 10
    val hr = msec / (1000 * 60 * 60)
    msec -= hr * (1000 * 60 * 60)
    val min = msec / (1000 * 60)
    msec -= min * (1000 * 60)
    val sec = msec / 1000
    msec -= sec * 1000

    val delimiter = if (comma) "," else "."
    return String.format("%02d:%02d:%02d%s%03d", hr, min, sec, delimiter, msec)
}

private fun isArmEabiV7a(): Boolean {
    return Build.SUPPORTED_ABIS[0].equals("armeabi-v7a")
}

private fun isArmEabiV8a(): Boolean {
    return Build.SUPPORTED_ABIS[0].equals("arm64-v8a")
}

private fun cpuInfo(): String? {
    val TAG = "LibWhisper"
    return try {
        File("/proc/cpuinfo").inputStream().bufferedReader().use {
            it.readText()
        }
    } catch (e: Exception) {
        Log.w(TAG, "Couldn't read /proc/cpuinfo", e)
        null
    }
}
