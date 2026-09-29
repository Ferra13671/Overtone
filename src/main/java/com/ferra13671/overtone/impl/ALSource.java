package com.ferra13671.overtone.impl;

import com.ferra13671.overtone.api.SoundBuffer;
import com.ferra13671.overtone.api.SoundSource;
import lombok.Getter;
import org.joml.Vector3f;
import org.lwjgl.openal.AL11;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

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

    @Getter
    private float x = 0f;
    @Getter
    private float y = 0f;
    @Getter
    private float z = 0f;

    @Getter
    private float yaw = 0f;
    @Getter
    private float elevation = 0f;
    @Getter
    private float roll = 0f;

    @Getter
    private final Vector3f lookVector = new Vector3f(0f, 0f, -1f);
    @Getter
    private final Vector3f upVector = new Vector3f(0f, 1f, 0f);
    private final FloatBuffer cacheBuffer = MemoryUtil.memAllocFloat(6);

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
        MemoryUtil.memFree(this.cacheBuffer);
        AL11.alDeleteSources(getHandle());
    }

    @Override
    public Vector3f getPosition() {
        return new Vector3f(getX(), getY(), getZ());
    }

    @Override
    public void setPosition(Vector3f position) {
        if (
                this.x != position.x() ||
                        this.y != position.y() ||
                        this.z != position.z()
        ) {
            this.x = position.x();
            this.y = position.y();
            this.z = position.z();

            this.backend.ensureScene(this.scene);
            AL11.alListener3f(AL11.AL_POSITION, this.x, this.y, this.z);
        }
    }

    @Override
    public void setRotation(float yaw, float elevation) {
        setRotation(yaw, elevation, getRoll());
    }

    @Override
    public void setRotation(float yaw, float elevation, float roll) {
        if (
                this.yaw != yaw ||
                        this.elevation != elevation ||
                        this.roll != roll
        ) {
            this.yaw = yaw;
            this.elevation = elevation;
            this.roll = roll;

            recalculateRotation();
        }
    }

    private void recalculateRotation() {
        float cosPitch = (float) Math.cos(this.getElevation());
        float sinPitch = (float) Math.sin(this.getElevation());
        float cosYaw   = (float) Math.cos(getYaw());
        float sinYaw   = (float) Math.sin(getYaw());

        this.lookVector.set(
                -sinYaw * cosPitch,
                sinPitch,
                -cosYaw * cosPitch
        );
        this.upVector.set(
                sinYaw * sinPitch,
                cosPitch,
                cosYaw * sinPitch
        );

        if (getRoll() != 0f)
            this.upVector.rotateAxis(getRoll(), this.lookVector.x, this.lookVector.y, this.lookVector.z);

        this.cacheBuffer
                .put(0, this.lookVector.x).put(1, this.lookVector.y).put(2, this.lookVector.z)
                .put(3, this.upVector.x).put(4, this.upVector.y).put(5, this.upVector.z);

        this.backend.ensureScene(this.scene);
        AL11.alListenerfv(AL11.AL_ORIENTATION, this.cacheBuffer);
    }
}
