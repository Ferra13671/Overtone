package com.ferra13671.overtone;

import org.lwjgl.openal.AL11;

public class SoundSource implements AutoCloseable {
    private final int id;

    public SoundSource() {
        this.id = AL11.alGenSources();
    }

    public void play() {
        AL11.alSourcePlay(this.id);
    }

    public void pause() {
        AL11.alSourcePause(this.id);
    }

    public void stop() {
        AL11.alSourceStop(this.id);
    }

    public void rewind() {
        AL11.alSourceRewind(this.id);
    }

    public boolean isPlaying() {
        return AL11.alGetSourcei(this.id, AL11.AL_SOURCE_STATE) == AL11.AL_PLAYING;
    }

    public void setBuffer(SoundBuffer buffer) {
        AL11.alSourcei(this.id, AL11.AL_BUFFER, buffer.id());
    }

    public void setGain(float gain) {
        AL11.alSourcef(this.id, AL11.AL_GAIN, gain);
    }

    public void setPitch(float pitch) {
        AL11.alSourcef(this.id, AL11.AL_PITCH, pitch);
    }

    public void setLooping(boolean looping) {
        AL11.alSourcei(this.id, AL11.AL_LOOPING, looping ? AL11.AL_TRUE : AL11.AL_FALSE);
    }

    @Override
    public void close() {
        AL11.alDeleteSources(this.id);
    }
}
