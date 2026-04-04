cmake_minimum_required(VERSION 3.22.1)

project("Assistant")

# Build our own native library
add_library (${CMAKE_PROJECT_NAME} SHARED
        jni.cpp
        SimpleNoiseMaker.cpp
        LiveEffectEngine.cpp
        whisper/whisper.cpp
        ggml/src/ggml.c
        ggml/src/ggml-threading.cpp
        ggml/src/ggml-backend-reg.cpp
        ggml/src/ggml-backend.cpp
        ggml/src/ggml-alloc.c
        ggml/src/ggml-quants.c
        ggml/src/ggml-cpu/ggml-cpu.c
        ggml/src/ggml-cpu/ggml-cpu.cpp
        ggml/src/ggml-cpu/traits.cpp
        ggml/src/ggml-cpu/vec.cpp
        ggml/src/ggml-cpu/ops.cpp
        ggml/src/ggml-cpu/binary-ops.cpp
        ggml/src/ggml-cpu/unary-ops.cpp
        ggml/src/ggml-cpu/quants.c
        ggml/src/ggml-cpu/arch/arm/quants.c
)

include_directories(${CMAKE_CURRENT_LIST_DIR})
include_directories(${CMAKE_CURRENT_LIST_DIR}/whisper)
include_directories(${CMAKE_CURRENT_LIST_DIR}/ggml/include)
include_directories(${CMAKE_CURRENT_LIST_DIR}/ggml/src)
include_directories(${CMAKE_CURRENT_LIST_DIR}/ggml/src/ggml-cpu)

# Find the Oboe package
find_package (oboe REQUIRED CONFIG)

# Specify the libraries which our native library is dependent on, including Oboe
target_link_libraries(${CMAKE_PROJECT_NAME} android log oboe::oboe)

target_compile_definitions(${CMAKE_PROJECT_NAME} PRIVATE
        GGML_COMMIT="${GGML_BUILD_COMMIT}"
        GGML_VERSION="${GGML_INSTALL_VERSION}"
        WHISPER_VERSION="${PROJECT_VERSION}"
        ${target_name} PUBLIC GGML_USE_CPU
)
