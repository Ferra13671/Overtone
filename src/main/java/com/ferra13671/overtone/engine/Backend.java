package com.ferra13671.overtone.engine;

import com.ferra13671.overtone.SoundBuffer;
import com.ferra13671.overtone.SoundSource;

public interface Backend {

    void tick();

    void playSource(SoundSource source);

    void pauseSource(SoundSource source);

    void stopSource(SoundSource source);

    void rewindSource(SoundSource source);

    void onChangeBuffer(SoundSource source);

    void onCloseBuffer(SoundBuffer buffer);

    void onCloseSource(SoundSource source);

    void close();
}
