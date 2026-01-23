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
        jni.c
        whisper.cpp/src/whisper.cpp
        whisper.cpp/ggml/src/ggml.c
        whisper.cpp/ggml/src/ggml.cpp
        whisper.cpp/ggml/src/ggml-threading.cpp
        whisper.cpp/ggml/src/ggml-backend-reg.cpp
        whisper.cpp/ggml/src/ggml-backend.cpp
        whisper.cpp/ggml/src/ggml-alloc.c
        whisper.cpp/ggml/src/ggml-quants.c
        whisper.cpp/ggml/src/ggml-cpu/ggml-cpu.cpp
        whisper.cpp/ggml/src/ggml-cpu/ggml-cpu.c
        whisper.cpp/ggml/src/ggml-cpu/traits.cpp
        whisper.cpp/ggml/src/ggml-cpu/vec.cpp
        whisper.cpp/ggml/src/ggml-cpu/ops.cpp
        whisper.cpp/ggml/src/ggml-cpu/binary-ops.cpp
        whisper.cpp/ggml/src/ggml-cpu/unary-ops.cpp
        whisper.cpp/ggml/src/ggml-cpu/quants.h
        whisper.cpp/ggml/src/ggml-cpu/quants.c
        whisper.cpp/ggml/src/ggml-cpu/arch/arm/quants.c
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
)
