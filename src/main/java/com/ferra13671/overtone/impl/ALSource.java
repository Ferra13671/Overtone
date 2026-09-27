package com.ferra13671.overtone.impl;

import com.ferra13671.overtone.api.SoundBuffer;
import com.ferra13671.overtone.api.SoundSource;
import org.lwjgl.openal.AL11;

final class ALSource implements SoundSource {
    private final int id;

    public ALSource() {
        this.id = AL11.alGenSources();
    }

    @Override
    public void play() {
        AL11.alSourcePlay(this.id);
    }

    @Override
    public void pause() {
        AL11.alSourcePause(this.id);
    }

    @Override
    public void stop() {
        AL11.alSourceStop(this.id);
    }

    @Override
    public void rewind() {
        AL11.alSourceRewind(this.id);
    }

    @Override
    public boolean isPlaying() {
        return AL11.alGetSourcei(this.id, AL11.AL_SOURCE_STATE) == AL11.AL_PLAYING;
    }

    @Override
    public void setBuffer(SoundBuffer buffer) {
        AL11.alSourcei(this.id, AL11.AL_BUFFER, ((ALBuffer) buffer).getHandler());
    }

    @Override
    public void setGain(float gain) {
        AL11.alSourcef(this.id, AL11.AL_GAIN, gain);
    }

    @Override
    public void setPitch(float pitch) {
        AL11.alSourcef(this.id, AL11.AL_PITCH, pitch);
    }

    @Override
    public void setLooping(boolean looping) {
        AL11.alSourcei(this.id, AL11.AL_LOOPING, looping ? AL11.AL_TRUE : AL11.AL_FALSE);
    }

    @Override
    public void close() {
        AL11.alDeleteSources(this.id);
    }
}
