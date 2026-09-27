package com.ferra13671.overtone.api;

import org.lwjgl.openal.ALCCapabilities;

public interface Backend {

    ALCCapabilities getDeviceCapabilities();

    Scene createScene();

    Scene getActiveScene();

    void ensureScene(Scene scene);

    SoundBuffer createBuffer();

    void close();
}
