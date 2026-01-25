# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html.
# For more examples on how to use CMake, see https://github.com/android/ndk-samples.

# Sets the minimum CMake version required for this project.
cmake_minimum_required(VERSION 3.22.1)

# Declares the project name. The project name can be accessed via ${ PROJECT_NAME},
# Since this is the top level CMakeLists.txt, the project name is also accessible
# with ${CMAKE_PROJECT_NAME} (both CMake variables are in-sync within the top level
# build script scope).
project("assistant")

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.
#
# In this top level CMakeLists.txt, ${CMAKE_PROJECT_NAME} is used to define
# the target library name; in the sub-module's CMakeLists.txt, ${PROJECT_NAME}
# is preferred for the same purpose.
#
# In order to load a library into your app from Java/Kotlin, you must call
# System.loadLibrary() and pass the name of the library defined here;
# for GameActivity/NativeActivity derived applications, the same library name must be
# used in the AndroidManifest.xml file.
add_library(${CMAKE_PROJECT_NAME} SHARED
        # List C/C++ source files with relative paths to this CMakeLists.txt.
#        jni.c
#        whisper.cpp/src/whisper.cpp
#        whisper.cpp/ggml/src/ggml.c
#        whisper.cpp/ggml/src/ggml.cpp
#        whisper.cpp/ggml/src/ggml-threading.cpp
#        whisper.cpp/ggml/src/ggml-backend-reg.cpp
#        whisper.cpp/ggml/src/ggml-backend.cpp
#        whisper.cpp/ggml/src/ggml-alloc.c
#        whisper.cpp/ggml/src/ggml-quants.c
#        whisper.cpp/ggml/src/ggml-cpu/ggml-cpu.cpp
#        whisper.cpp/ggml/src/ggml-cpu/ggml-cpu.c
#        whisper.cpp/ggml/src/ggml-cpu/traits.cpp
#        whisper.cpp/ggml/src/ggml-cpu/vec.cpp
#        whisper.cpp/ggml/src/ggml-cpu/ops.cpp
#        whisper.cpp/ggml/src/ggml-cpu/binary-ops.cpp
#        whisper.cpp/ggml/src/ggml-cpu/unary-ops.cpp
#        whisper.cpp/ggml/src/ggml-cpu/quants.c
#        whisper.cpp/ggml/src/ggml-cpu/arch/arm/quants.c

        ai_chat.cpp
        llama.cpp/common/chat.cpp
        llama.cpp/common/log.cpp
        llama.cpp/common/common.cpp
        llama.cpp/common/sampling.cpp
        llama.cpp/common/json-schema-to-grammar.cpp
        llama.cpp/common/chat-parser-xml-toolcall.cpp
        llama.cpp/common/chat-parser.cpp
        llama.cpp/common/regex-partial.cpp
        llama.cpp/common/json-partial.cpp
        llama.cpp/common/peg-parser.cpp
        llama.cpp/common/unicode.cpp
        llama.cpp/common/chat-peg-parser.cpp
        llama.cpp/common/jinja/caps.cpp
        llama.cpp/common/jinja/runtime.cpp
        llama.cpp/common/jinja/value.cpp
        llama.cpp/common/jinja/string.cpp
        llama.cpp/common/jinja/lexer.cpp
        llama.cpp/common/jinja/parser.cpp
        llama.cpp/ggml/src/gguf.cpp
        llama.cpp/ggml/src/ggml-opt.cpp
        llama.cpp/ggml/src/ggml.c
        llama.cpp/ggml/src/ggml-alloc.c
        llama.cpp/ggml/src/ggml-backend.cpp
        llama.cpp/ggml/src/ggml-backend-reg.cpp
        llama.cpp/ggml/src/ggml-quants.c
        llama.cpp/ggml/src/ggml-threading.cpp
        llama.cpp/ggml/src/ggml-cpu/ggml-cpu.cpp
        llama.cpp/ggml/src/ggml-cpu/ggml-cpu.c
        llama.cpp/ggml/src/ggml-cpu/traits.cpp
        llama.cpp/ggml/src/ggml-cpu/vec.cpp
        llama.cpp/ggml/src/ggml-cpu/ops.cpp
        llama.cpp/ggml/src/ggml-cpu/binary-ops.cpp
        llama.cpp/ggml/src/ggml-cpu/unary-ops.cpp
        llama.cpp/ggml/src/ggml-cpu/quants.c
        llama.cpp/ggml/src/ggml-cpu/arch/arm/quants.c
        llama.cpp/src/models/llama.cpp
        llama.cpp/src/models/llama-iswa.cpp
        llama.cpp/src/models/maincoder.cpp
        llama.cpp/src/models/deci.cpp
        llama.cpp/src/models/baichuan.cpp
        llama.cpp/src/models/falcon.cpp
        llama.cpp/src/models/grok.cpp
        llama.cpp/src/models/starcoder.cpp
        llama.cpp/src/models/refact.cpp
        llama.cpp/src/models/bert.cpp
        llama.cpp/src/models/modern-bert.cpp
        llama.cpp/src/models/neo-bert.cpp
        llama.cpp/src/models/bloom.cpp
        llama.cpp/src/models/mpt.cpp
        llama.cpp/src/models/stablelm.cpp
        llama.cpp/src/models/qwen.cpp
        llama.cpp/src/models/qwen2.cpp
        llama.cpp/src/models/dream.cpp
        llama.cpp/src/models/llada.cpp
        llama.cpp/src/models/llada-moe.cpp
        llama.cpp/src/models/rnd1.cpp
        llama.cpp/src/models/qwen2vl.cpp
        llama.cpp/src/models/qwen2moe.cpp
        llama.cpp/src/models/qwen3.cpp
        llama.cpp/src/models/qwen3moe.cpp
        llama.cpp/src/models/qwen3vl.cpp
        llama.cpp/src/models/qwen3vl-moe.cpp
        llama.cpp/src/models/phi2.cpp
        llama.cpp/src/models/phi3.cpp
        llama.cpp/src/models/plamo.cpp
        llama.cpp/src/models/plamo2.cpp
        llama.cpp/src/models/plamo3.cpp
        llama.cpp/src/models/gpt2.cpp
        llama.cpp/src/models/codeshell.cpp
        llama.cpp/src/models/orion.cpp
        llama.cpp/src/models/internlm2.cpp
        llama.cpp/src/models/gemma.cpp
        llama.cpp/src/models/gemma2-iswa.cpp
        llama.cpp/src/models/gemma3.cpp
        llama.cpp/src/models/gemma-embedding.cpp
        llama.cpp/src/models/starcoder2.cpp
        llama.cpp/src/models/mamba.cpp
        llama.cpp/src/models/jamba.cpp
        llama.cpp/src/models/graph-context-mamba.cpp
        llama.cpp/src/models/minicpm3.cpp
        llama.cpp/src/models/gemma3n-iswa.cpp
        llama.cpp/src/models/xverse.cpp
        llama.cpp/src/models/command-r.cpp
        llama.cpp/src/models/cohere2-iswa.cpp
        llama.cpp/src/models/dbrx.cpp
        llama.cpp/src/models/olmo.cpp
        llama.cpp/src/models/olmo2.cpp
        llama.cpp/src/models/olmoe.cpp
        llama.cpp/src/models/openelm.cpp
        llama.cpp/src/models/gptneox.cpp
        llama.cpp/src/models/arctic.cpp
        llama.cpp/src/models/deepseek.cpp
        llama.cpp/src/models/deepseek2.cpp
        llama.cpp/src/models/chatglm.cpp
        llama.cpp/src/models/glm4.cpp
        llama.cpp/src/models/glm4-moe.cpp
        llama.cpp/src/models/bitnet.cpp
        llama.cpp/src/models/t5-enc.cpp
        llama.cpp/src/models/t5-dec.cpp
        llama.cpp/src/models/jais.cpp
        llama.cpp/src/models/nemotron.cpp
        llama.cpp/src/models/nemotron-h.cpp
        llama.cpp/src/models/exaone.cpp
        llama.cpp/src/models/exaone4.cpp
        llama.cpp/src/models/exaone-moe.cpp
        llama.cpp/src/models/rwkv6.cpp
        llama.cpp/src/models/rwkv6qwen2.cpp
        llama.cpp/src/models/rwkv7.cpp
        llama.cpp/src/models/granite.cpp
        llama.cpp/src/models/granite-hybrid.cpp
        llama.cpp/src/models/chameleon.cpp
        llama.cpp/src/models/wavtokenizer-dec.cpp
        llama.cpp/src/models/plm.cpp
        llama.cpp/src/models/bailingmoe.cpp
        llama.cpp/src/models/bailingmoe2.cpp
        llama.cpp/src/models/seed-oss.cpp
        llama.cpp/src/models/rwkv6.cpp
        llama.cpp/src/models/rwkv7-base.cpp
        llama.cpp/src/models/rwkv6-base.cpp
        llama.cpp/src/models/arwkv7.cpp
        llama.cpp/src/models/dots1.cpp
        llama.cpp/src/models/arcee.cpp
        llama.cpp/src/models/afmoe.cpp
        llama.cpp/src/models/ernie4-5.cpp
        llama.cpp/src/models/ernie4-5-moe.cpp
        llama.cpp/src/models/hunyuan-moe.cpp
        llama.cpp/src/models/hunyuan-dense.cpp
        llama.cpp/src/models/smollm3.cpp
        llama.cpp/src/models/openai-moe-iswa.cpp
        llama.cpp/src/models/falcon-h1.cpp
        llama.cpp/src/models/lfm2.cpp
        llama.cpp/src/models/smallthinker.cpp
        llama.cpp/src/models/grovemoe.cpp
        llama.cpp/src/models/apertus.cpp
        llama.cpp/src/models/minimax-m2.cpp
        llama.cpp/src/models/cogvlm.cpp
        llama.cpp/src/models/pangu-embedded.cpp
        llama.cpp/src/models/qwen3next.cpp
        llama.cpp/src/models/mistral3.cpp
        llama.cpp/src/models/mimo2-iswa.cpp
        llama.cpp/src/llama.cpp
        llama.cpp/src/llama-impl.cpp
        llama.cpp/src/llama-model.cpp
        llama.cpp/src/llama-batch.cpp
        llama.cpp/src/llama-vocab.cpp
        llama.cpp/src/llama-model-loader.cpp
        llama.cpp/src/llama-arch.cpp
        llama.cpp/src/llama-mmap.cpp
        llama.cpp/src/llama-model-saver.cpp
        llama.cpp/src/llama-hparams.cpp
        llama.cpp/src/unicode.cpp
        llama.cpp/src/llama-context.cpp
        llama.cpp/src/llama-sampling.cpp
        llama.cpp/src/unicode-data.cpp
        llama.cpp/src/llama-grammar.cpp
        llama.cpp/src/llama-graph.cpp
        llama.cpp/src/llama-kv-cache.cpp
        llama.cpp/src/llama-memory-recurrent.cpp
        llama.cpp/src/llama-kv-cache-iswa.cpp
        llama.cpp/src/llama-memory.cpp
        llama.cpp/src/llama-chat.cpp
        llama.cpp/src/llama-memory-hybrid.cpp
        llama.cpp/src/llama-memory-hybrid-iswa.cpp
        llama.cpp/src/llama-adapter.cpp
        llama.cpp/src/llama-io.cpp
)

find_package(oboe REQUIRED CONFIG)

# Specifies libraries CMake should link to your target library. You
# can link libraries from various origins, such as libraries defined in this
# build script, prebuilt third-party libraries, or Android system libraries.
target_link_libraries(${CMAKE_PROJECT_NAME}
        # List libraries link to the target library
        android
        log
)

target_compile_definitions(${CMAKE_PROJECT_NAME} PRIVATE
        WHISPER_VERSION="${PROJECT_VERSION}"
        GGML_VERSION="${GGML_INSTALL_VERSION}"
        GGML_COMMIT="${GGML_BUILD_COMMIT}"
        ${target_name} PUBLIC GGML_USE_CPU
        LLAMA_BUILD_NUMBER ${BUILD_NUMBER}
        LLAMA_COMMIT="${LLAMA_BUILD_COMMIT}"
        LLAMA_COMPILER="${BUILD_COMPILER}"
        LLAMA_BUILD_TARGET="${BUILD_TARGET}"
)

set(CMAKE_C_STANDARD 11)
set(CMAKE_C_STANDARD_REQUIRED true)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED true)

set(CMAKE_C_FLAGS   "${CMAKE_C_FLAGS}"   CACHE STRING "" FORCE)
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS}" CACHE STRING "" FORCE)

# --------------------------------------------------------------------------
# AI Chat library
# --------------------------------------------------------------------------

if(DEFINED ANDROID_ABI)
    message(STATUS "Detected Android ABI: ${ANDROID_ABI}")
    if(ANDROID_ABI STREQUAL "arm64-v8a")
        set(GGML_SYSTEM_ARCH "ARM")
        set(GGML_CPU_KLEIDIAI ON)
        set(GGML_OPENMP ON)
    elseif(ANDROID_ABI STREQUAL "x86_64")
        set(GGML_SYSTEM_ARCH "x86")
        set(GGML_CPU_KLEIDIAI OFF)
        set(GGML_OPENMP OFF)
    else()
        message(FATAL_ERROR "Unsupported ABI: ${ANDROID_ABI}")
    endif()
endif()
