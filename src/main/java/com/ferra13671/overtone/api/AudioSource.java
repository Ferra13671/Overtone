package com.ferra13671.overtone.api;

public interface AudioSource extends AutoCloseable {

    void play();

    void pause();

    void stop();

    void rewind();

    boolean isPlaying();

    void setBuffer(AudioBuffer audioBuffer);

    void setGain(float gain);

    void setPitch(float pitch);

    void setLooping(boolean looping);

    @Override
    void close();
}
