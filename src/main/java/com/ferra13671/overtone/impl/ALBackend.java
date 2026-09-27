package com.ferra13671.overtone.impl;

import com.ferra13671.overtone.api.Backend;
import com.ferra13671.overtone.api.Scene;
import com.ferra13671.overtone.api.SoundBuffer;
import lombok.AccessLevel;
import lombok.Getter;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALCCapabilities;

import java.util.ArrayList;
import java.util.List;

public final class ALBackend implements Backend {
    private final ALDevice device;
    @Getter(AccessLevel.PROTECTED)
    private final List<Scene> scenes = new ArrayList<>();
    private Scene scene;

    public ALBackend() {
        this.device = new ALDevice(null, this);
        ensureScene(createScene());
    }

    @Override
    public ALCCapabilities getDeviceCapabilities() {
        return this.device.getCapabilities();
    }

    @Override
    public Scene createScene() {
        return new ALScene(this.device, this);
    }

    @Override
    public Scene getActiveScene() {
        return this.scene;
    }

    @Override
    public void ensureScene(Scene scene) {
        if (this.scene != scene) {
            if (!ALC10.alcMakeContextCurrent(scene.getHandle()))
                throw new IllegalStateException("Cannot make scene current.");

            this.scene = scene;
            AL.setCurrentProcess(this.scene.getCapabilities());
        }
    }

    @Override
    public SoundBuffer createBuffer() {
        return new ALBuffer();
    }

    @Override
    public void close() {
        this.device.close();
    }
}
