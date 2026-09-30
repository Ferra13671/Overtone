package com.ferra13671.overtone;

import lombok.Getter;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

public final class ALDevice {
    @Getter
    private final long handle;
    @Getter
    private final ALCCapabilities capabilities;

    public ALDevice(String name) {
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
        ALC10.alcCloseDevice(this.handle);
    }
}
