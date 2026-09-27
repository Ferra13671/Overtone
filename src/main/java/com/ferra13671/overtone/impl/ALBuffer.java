package com.ferra13671.overtone.impl;

import com.ferra13671.overtone.api.SoundBuffer;
import com.ferra13671.overtone.api.SoundFormat;
import lombok.Getter;
import org.lwjgl.openal.AL11;

import java.nio.ShortBuffer;

final class ALBuffer implements SoundBuffer {
    @Getter
    private final int handle;

    public ALBuffer() {
        this.handle = AL11.alGenBuffers();
    }

    @Override
    public void uploadData(SoundFormat soundFormat, ShortBuffer pcm, int sampleRate) {
        AL11.alBufferData(
                this.handle,
                soundFormat.id,
                pcm,
                sampleRate
        );
    }

    @Override
    public void close() {
        AL11.alDeleteBuffers(this.handle);
    }
}
