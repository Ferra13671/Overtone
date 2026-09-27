package com.ferra13671.overtone.impl;

import com.ferra13671.overtone.api.AudioContext;
import lombok.Getter;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALCapabilities;

import java.nio.IntBuffer;

final class ALContext implements AudioContext {
    @Getter
    private final ALDevice device;
    private final long handle;
    private ALCapabilities capabilities;

    public ALContext(ALDevice device) {
        this.device = device;
        this.handle = ALC10.alcCreateContext(device.getHandle(), (IntBuffer) null);
        getDevice().getContexts().add(this);
    }

    @Override
    public void suspend() {
        ALC10.alcSuspendContext(this.handle);
    }

    @Override
    public void process() {
        ALC10.alcProcessContext(this.handle);
    }

    @Override
    public void makeCurrent() {
        if (!ALC10.alcMakeContextCurrent(this.handle))
            throw new IllegalStateException("Cannot make context current.");

        if (this.capabilities == null)
            this.capabilities = AL.createCapabilities(this.device.getCapabilities());
        else
            AL.setCurrentProcess(this.capabilities);

        getDevice().getBackend().setCurrentContext(this);
    }

    @Override
    public void close() {
        ALC10.alcCloseDevice(this.handle);
        getDevice().getContexts().remove(this);
    }
}
