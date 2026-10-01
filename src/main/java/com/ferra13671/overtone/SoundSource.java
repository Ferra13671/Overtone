package com.ferra13671.overtone;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
public sealed class SoundSource implements AutoCloseable
        permits SpatialSoundSource {
    private SoundBuffer soundBuffer = null;
    private float volume = 1f;
    private float pitch = 1f;
    private boolean looping = false;
    @Setter(AccessLevel.PACKAGE)
    private SoundState state = SoundState.Stopped;

    @Getter(AccessLevel.PACKAGE)
    @Setter(AccessLevel.PACKAGE)
    private boolean dirty = true;
    @Getter(AccessLevel.PACKAGE)
    @Setter(AccessLevel.PACKAGE)
    private int backendHandle = -1;
    @Getter(AccessLevel.PACKAGE)
    @Setter(AccessLevel.PACKAGE)
    private int backendSampleOffset = 0;

    SoundSource() {}

    public void play() {
        Overtone.getEngine().playSource(this);
    }

    public void pause() {
        Overtone.getEngine().pauseSource(this);
    }

    public void stop() {
        Overtone.getEngine().stopSource(this);
    }

    public void rewind() {
        Overtone.getEngine().rewindSource(this);
    }

    public void setSoundBuffer(SoundBuffer soundBuffer) {
        if (!Objects.equals(this.soundBuffer, soundBuffer)) {
            this.soundBuffer = soundBuffer;

            Overtone.getEngine().onChangeBuffer(this);
        }
    }

    public void setVolume(float volume) {
        if (this.volume != volume) {
            this.volume = volume;
            setDirty(true);
        }
    }

    public void setPitch(float pitch) {
        if (this.pitch != pitch) {
            this.pitch = pitch;
            setDirty(true);
        }
    }

    public void setLooping(boolean looping) {
        if (this.looping != looping) {
            this.looping = looping;
            setDirty(true);
        }
    }

    @Override
    public void close() {
        Overtone.closeSource(this);
    }
}
