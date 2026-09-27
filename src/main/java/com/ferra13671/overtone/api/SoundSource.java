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

    float getVolume();

    void setVolume(float volume);

    float getPitch();

    void setPitch(float pitch);

    boolean isLooping();

    void setLooping(boolean looping);

    @Override
    void close();
}
