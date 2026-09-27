package com.ferra13671.overtone.impl;

import com.ferra13671.overtone.api.SoundBuffer;
import com.ferra13671.overtone.api.SoundSource;
import lombok.Getter;
import org.lwjgl.openal.AL11;

final class ALSource implements SoundSource {
    private final ALScene scene;
    private final ALBackend backend;
    @Getter
    private final int handle;
    private SoundBuffer buffer = null;
    @Getter
    private float volume = 1f;
    @Getter
    private float pitch = 1f;
    @Getter
    private boolean looping = false;

    public ALSource(ALScene scene, ALBackend backend) {
        this.scene = scene;
        this.backend = backend;

        backend.ensureScene(scene);
        this.handle = AL11.alGenSources();
    }

    @Override
    public void play() {
        this.backend.ensureScene(this.scene);
        AL11.alSourcePlay(getHandle());
    }

    @Override
    public void pause() {
        this.backend.ensureScene(this.scene);
        AL11.alSourcePause(getHandle());
    }

    @Override
    public void stop() {
        this.backend.ensureScene(this.scene);
        AL11.alSourceStop(getHandle());
    }

    @Override
    public void rewind() {
        this.backend.ensureScene(this.scene);
        AL11.alSourceRewind(getHandle());
    }

    @Override
    public boolean isPlaying() {
        this.backend.ensureScene(this.scene);
        return AL11.alGetSourcei(getHandle(), AL11.AL_SOURCE_STATE) == AL11.AL_PLAYING;
    }

    @Override
    public void awaitPlaybackCompletion() {
        while (isPlaying()) {
            Thread.onSpinWait();
        }
    }

    @Override
    public void setBuffer(SoundBuffer buffer) {
        if (this.buffer != buffer) {
            this.backend.ensureScene(this.scene);
            AL11.alSourcei(getHandle(), AL11.AL_BUFFER, buffer.getHandle());

            this.buffer = buffer;
        }
    }

    @Override
    public void setVolume(float volume) {
        if (this.volume != volume) {
            this.backend.ensureScene(this.scene);
            AL11.alSourcef(getHandle(), AL11.AL_GAIN, volume);

            this.volume = volume;
        }
    }

    @Override
    public void setPitch(float pitch) {
        if (this.pitch != pitch) {
            this.backend.ensureScene(this.scene);
            AL11.alSourcef(getHandle(), AL11.AL_PITCH, pitch);

            this.pitch = pitch;
        }
    }

    @Override
    public void setLooping(boolean looping) {
        if (this.looping != looping) {
            this.backend.ensureScene(this.scene);
            AL11.alSourcei(getHandle(), AL11.AL_LOOPING, looping ? AL11.AL_TRUE : AL11.AL_FALSE);

            this.looping = looping;
        }
    }

    @Override
    public void close() {
        this.backend.ensureScene(this.scene);
        AL11.alDeleteSources(getHandle());
    }
}
