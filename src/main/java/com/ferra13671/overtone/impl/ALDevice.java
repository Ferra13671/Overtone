package com.ferra13671.overtone.impl;

import com.ferra13671.overtone.api.AudioContext;
import com.ferra13671.overtone.api.AudioDevice;
import lombok.AccessLevel;
import lombok.Getter;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

final class ALDevice implements AudioDevice {
    @Getter(AccessLevel.PACKAGE)
    private final ALBackend backend;
    @Getter(AccessLevel.PACKAGE)
    private final long handle;
    @Getter
    private final ALCCapabilities capabilities;
    @Getter(AccessLevel.PACKAGE)
    private final Set<AudioContext> contexts = new HashSet<>();

    public ALDevice(String name, ALBackend backend) {
        this.backend = backend;
        ByteBuffer buffer = name != null ? MemoryUtil.memUTF8(name) : null;

        try {
            this.handle = ALC10.alcOpenDevice(buffer);
            if (this.handle == MemoryUtil.NULL)
                throw new IllegalStateException("Cannot open device: " + name);

            this.capabilities = ALC.createCapabilities(this.handle);
        } finally {
            if (buffer != null)
                MemoryUtil.memFree(buffer);
        }
    }

    @Override
    public Set<AudioContext> getAllContexts() {
        return Collections.unmodifiableSet(this.contexts);
    }

    @Override
    public AudioContext createContext() {
        return new ALContext(this);
    }

    public void close() {
        for (AudioContext context : Set.copyOf(getContexts()))
            context.close();

        ALC10.alcCloseDevice(this.handle);
    }
}
