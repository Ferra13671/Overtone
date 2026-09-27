package com.ferra13671.overtone.api;

import java.nio.ShortBuffer;

public interface AudioBuffer extends AutoCloseable {

    void uploadData(SoundFormat soundFormat, ShortBuffer pcm, int sampleRate);

    @Override
    void close();
}
