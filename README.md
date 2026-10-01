<p align="center">
    <img src="https://img.shields.io/github/downloads/Ferra13671/Overtone/total" alt="Downloads"/>
    <a href="https://github.com/Ferra13671/Overtone/blob/main/LICENSE.md"> <img src="https://img.shields.io/badge/license-EPL%202.0-blue.svg" alt="License"/> </a> 
    <a href="https://github.com/Ferra13671/Overtone/releases"> <img src="https://img.shields.io/github/v/release/Ferra13671/Overtone" alt="Release"/> </a>
</p>

# Overtone

A lightweight audio library for Java built on OpenAL (via LWJGL 3). It provides source virtualization,
2D/3D positioning, OGG/WAV decoding, and automatic handling of audio device changes.

## Requirements
* Java 21 or later. 
* LWJGL 3.3.3+ with lwjgl-openal and lwjgl-stb modules.
* OpenAL Soft 1.20+.
* JOML 1.10.x.

## Installing
### Gradle
Add the Ferra13671 repository to build.gradle:
```groovy
repositories {
    maven {
        name = "ferra13671-maven"
        url = "https://ferra13671.github.io/maven/"
    }
}
```
```groovy
dependencies {
    implementation("com.ferra13671:overtone:${overtone_version}")
}
```

## Quick start
```java
import com.ferra13671.overtone.*;

public class Example {
    public static void main(String[] args) throws Exception {
        Overtone.init();

        try (SoundBuffer buffer = Overtone.createBuffer(
                SoundFormat.Mono,
                PCMGenerator.sine(440f, 1000, Overtone.DEFAULT_SAMPLE_RATE),
                Overtone.DEFAULT_SAMPLE_RATE)) {

            try (SoundSource source = Overtone.create2DSource()) {
                source.setSoundBuffer(buffer);
                source.setVolume(0.5f);
                source.setLooping(true);
                source.play();

                while (source.getState() == SoundState.Playing) {
                    Overtone.tick();
                    Thread.sleep(16);
                }
            }
        }

        Overtone.close();
    }
}
```

Mandatory requirement: `Overtone.tick()` must be called regularly (once per frame is recommended).
Without it, source states are not updated and device changes are not processed.

## Structure

| Class                | Responsibility                                                      |
|----------------------|---------------------------------------------------------------------|
| `Overtone`           | Entry point. Registry of buffers and sources. Stores `AudioEngine`. |
| `AudioEngine`        | Manages the lifecycle of sound system. Reacts to device changes.    |
| `SoundBuffer`        | PCM buffer. Used in `SoundSource`.                                  |
| `SoundSource`        | 2D source without position.                                         |
| `SpatialSoundSource` | 3D source with position and orientation.                            |
| `Listener`           | Global listener of 3D scene.                                        |
| `Decoder`            | Audio format decoder. `OGGDecoder`, `WAVDecoder`.                   |
| `PCMGenerator`       | Utility for generation simple sounds.                               |