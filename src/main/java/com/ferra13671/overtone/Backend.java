package com.ferra13671.overtone;

public interface Backend {

    void tick();

    void playSource(SoundSource source);

    void pauseSource(SoundSource source);

    void stopSource(SoundSource source);

    void rewindSource(SoundSource source);

    void onChangeBuffer(SoundSource source);

    void onChangeVolume(SoundSource source);

    void onChangePitch(SoundSource source);

    void onChangeLooping(SoundSource source);

    void onCloseBuffer(SoundBuffer buffer);

    void onCloseSource(SoundSource source);

    void close();
}
