package com.ferra13671.overtone.impl;

import com.ferra13671.overtone.api.Scene;
import lombok.Getter;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;

//TODO resource tracking
final class ALDevice {
    @Getter
    private final ALBackend backend;
    @Getter
    private final long handle;
    @Getter
    private final ALCCapabilities capabilities;
    @Getter
    private final Set<Scene> scenes = new HashSet<>();

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

    public void close() {
        for (Scene context : Set.copyOf(getScenes()))
            context.close();

        ALC10.alcCloseDevice(this.handle);
    }
}
