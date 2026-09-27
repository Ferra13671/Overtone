package com.ferra13671.overtone.api;

import com.ferra13671.overtone.HandleableInt;

import java.nio.ShortBuffer;

public interface SoundBuffer extends AutoCloseable, HandleableInt {

    void uploadData(SoundFormat soundFormat, ShortBuffer pcm, int sampleRate);

    @Override
    void close();
}
