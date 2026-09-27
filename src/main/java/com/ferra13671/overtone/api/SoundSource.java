package com.ferra13671.overtone.api;

import com.ferra13671.overtone.HandleableInt;

public interface SoundSource extends AutoCloseable, HandleableInt {

    void play();

    void pause();

    void stop();

    void rewind();

    boolean isPlaying();

    void awaitPlaybackCompletion();

    void setBuffer(SoundBuffer soundBuffer);

    void setGain(float gain);

    void setPitch(float pitch);

    void setLooping(boolean looping);

    @Override
    void close();
}
