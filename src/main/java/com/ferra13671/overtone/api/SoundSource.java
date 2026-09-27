package com.ferra13671.overtone.api;

public interface SoundSource extends AutoCloseable {

    void play();

    void pause();

    void stop();

    void rewind();

    boolean isPlaying();

    void setBuffer(SoundBuffer soundBuffer);

    void setGain(float gain);

    void setPitch(float pitch);

    void setLooping(boolean looping);

    @Override
    void close();
}
