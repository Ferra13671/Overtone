package com.ferra13671.overtone.impl;

import com.ferra13671.overtone.api.AudioBuffer;
import com.ferra13671.overtone.api.SoundFormat;
import lombok.AccessLevel;
import lombok.Getter;
import org.lwjgl.openal.AL11;

import java.nio.ShortBuffer;

final class ALBuffer implements AudioBuffer {
    @Getter(AccessLevel.PACKAGE)
    private final int handler;

    public ALBuffer() {
        this.handler = AL11.alGenBuffers();
    }

    @Override
    public void uploadData(SoundFormat soundFormat, ShortBuffer pcm, int sampleRate) {
        AL11.alBufferData(
                this.handler,
                soundFormat.id,
                pcm,
                sampleRate
        );
    }

    @Override
    public void close() {
        AL11.alDeleteBuffers(this.handler);
    }
}
