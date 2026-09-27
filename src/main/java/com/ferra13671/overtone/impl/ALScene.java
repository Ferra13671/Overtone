package com.ferra13671.overtone.impl;

import com.ferra13671.overtone.api.Scene;
import com.ferra13671.overtone.api.SoundSource;
import lombok.Getter;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALCapabilities;

import java.nio.IntBuffer;

final class ALScene implements Scene {
    private final ALDevice device;
    private final ALBackend backend;
    @Getter
    private final long handle;
    @Getter
    private final ALListener listener;
    private ALCapabilities capabilities;

    public ALScene(ALDevice device, ALBackend backend) {
        this.device = device;
        this.backend = backend;

        this.handle = ALC10.alcCreateContext(device.getHandle(), (IntBuffer) null);
        backend.getScenes().add(this);

        this.listener = new ALListener(this, backend);
    }

    @Override
    public void suspend() {
        ALC10.alcSuspendContext(getHandle());
    }

    @Override
    public void process() {
        ALC10.alcProcessContext(getHandle());
    }

    //TODO source pool
    @Override
    public SoundSource createSource() {
        return new ALSource(this, this.backend);
    }

    @Override
    public ALCapabilities getCapabilities() {
        if (this.capabilities == null) {
            this.backend.ensureScene(this);
            this.capabilities = AL.createCapabilities(this.device.getCapabilities());
        }

        return this.capabilities;
    }

    @Override
    public void close() {
        this.listener.close();
        ALC10.alcDestroyContext(getHandle());
        this.backend.getScenes().remove(this);
    }
}
