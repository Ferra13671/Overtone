package com.ferra13671.overtone;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
public class SoundSource implements AutoCloseable {
    private SoundBuffer soundBuffer = null;
    private float volume = 1f;
    private float pitch = 1f;
    private boolean looping = false;
    @Setter(AccessLevel.PACKAGE)
    private SoundState state = SoundState.Stopped;

    @Getter(AccessLevel.PACKAGE)
    @Setter(AccessLevel.PACKAGE)
    private int backendHandle = -1;
    @Getter(AccessLevel.PACKAGE)
    @Setter(AccessLevel.PACKAGE)
    private int backendSampleOffset = 0;

    SoundSource() {}

    public void play() {
        Overtone.getBackend().playSource(this);
    }

    public void pause() {
        Overtone.getBackend().pauseSource(this);
    }

    public void stop() {
        Overtone.getBackend().stopSource(this);
    }

    public void rewind() {
        Overtone.getBackend().rewindSource(this);
    }

    public void setSoundBuffer(SoundBuffer soundBuffer) {
        if (!Objects.equals(this.soundBuffer, soundBuffer)) {
            this.soundBuffer = soundBuffer;

            Overtone.getBackend().onChangeBuffer(this);
        }
    }

    public void setVolume(float volume) {
        if (this.volume != volume) {
            this.volume = volume;

            Overtone.getBackend().onChangeVolume(this);
        }
    }

    public void setPitch(float pitch) {
        if (this.pitch != pitch) {
            this.pitch = pitch;

            Overtone.getBackend().onChangePitch(this);
        }
    }

    public void setLooping(boolean looping) {
        if (this.looping != looping) {
            this.looping = looping;

            Overtone.getBackend().onChangeLooping(this);
        }
    }

    @Override
    public void close() {
        Overtone.closeSource(this);
    }
}
