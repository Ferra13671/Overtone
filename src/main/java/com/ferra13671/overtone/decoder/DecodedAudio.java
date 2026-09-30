package com.ferra13671.overtone.decoder;

import org.lwjgl.system.MemoryUtil;

import java.nio.ShortBuffer;

public record DecodedAudio(ShortBuffer pcm, int channels, int sampleRate) implements AutoCloseable {

    @Override
    public void close() {
        MemoryUtil.memFree(pcm());
    }
}
