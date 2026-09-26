package com.ferra13671.overtone;

import org.lwjgl.openal.AL11;

public record SoundBuffer(int id, SoundFormat format, int sampleRate) implements AutoCloseable {

    @Override
    public void close() {
        AL11.alDeleteBuffers(id());
    }
}
