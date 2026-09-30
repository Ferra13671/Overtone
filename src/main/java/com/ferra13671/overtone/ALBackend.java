package com.ferra13671.overtone;

import org.joml.Vector3fc;
import org.lwjgl.openal.*;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public final class ALBackend implements Backend {
    private final long context;
    private final ALCapabilities contextCapabilities;

    private final SourcePool sourcePool;
    private final List<SoundSource> allocatedSources = new CopyOnWriteArrayList<>();
    private final Set<Integer> activeBuffers = new HashSet<>();

    private final FloatBuffer sourceRotationCacheBuffer = MemoryUtil.memAllocFloat(6);

    ALBackend(ALDevice device) {
        this.context = ALC11.alcCreateContext(device.getHandle(), (IntBuffer) null);
        if (!ALC11.alcMakeContextCurrent(this.context))
            throw new IllegalStateException("Cannot make current context");
        this.contextCapabilities = AL.createCapabilities(device.getCapabilities());

        this.sourcePool = new SourcePool(32);

        for (SoundSource soundSource : Overtone.getSources()) {
            System.out.println(soundSource.getState());
            if (soundSource.getState() == SoundState.Playing) {
                playSource(soundSource);
            }
        }
    }

    @Override
    public void tick() {
        //TODO recreate the device if all sources stop within a single tick (provided there are more than 5)
        for (SoundSource source : this.allocatedSources) {
            if (source.getBackendHandle() == -1) {
                System.err.println("Synchronization error: allocated SoundSource not loaded into OpenAL!");
                this.allocatedSources.remove(source);
                continue;
            }

            if (source.isDirty())
                applySourceState(source);

            if (AL11.alGetSourcei(source.getBackendHandle(), AL11.AL_SOURCE_STATE) != AL11.AL_PLAYING)
                stopSource(source);
        }
    }

    private void applySourceState(SoundSource source) {
        int handle = source.getBackendHandle();

        AL11.alSourcef(handle, AL11.AL_GAIN, source.getVolume());
        AL11.alSourcef(handle, AL11.AL_PITCH, source.getPitch());
        AL11.alSourcei(handle, AL11.AL_LOOPING, source.isLooping() ? AL11.AL_TRUE : AL11.AL_FALSE);

        if (source instanceof SpatialSoundSource sss) {
            Vector3fc position = sss.getPosition();
            Vector3fc lookVector = sss.getLookVector();
            Vector3fc upVector = sss.getUpVector();

            AL11.alSourcei(handle, AL11.AL_SOURCE_RELATIVE, AL11.AL_FALSE);
            AL11.alSource3f(handle, AL11.AL_POSITION, position.x(), position.y(), position.z());

            this.sourceRotationCacheBuffer
               .put(0, lookVector.x()).put(1, lookVector.y()).put(2, lookVector.z())
               .put(3, upVector.x()).put(4, upVector.y()).put(5, upVector.z());
            AL11.alSourcefv(handle, AL11.AL_ORIENTATION, this.sourceRotationCacheBuffer);
        } else {
            AL11.alSourcei(handle, AL11.AL_SOURCE_RELATIVE, AL11.AL_TRUE);
            AL11.alSource3f(handle, AL11.AL_POSITION, 0f, 0f, 0f);
        }

        source.setDirty(false);
    }

    @Override
    public void playSource(SoundSource source) {
        if (source.getSoundBuffer() == null)
            return;

        int handle;
        if (source.getBackendHandle() == -1) {
            handle = this.sourcePool.acquire();
            this.allocatedSources.add(source);
            source.setBackendHandle(handle);
        } else
            handle = source.getBackendHandle();

        allocateBuffer(source.getSoundBuffer());

        AL11.alSourcei(handle, AL11.AL_BUFFER, source.getSoundBuffer().getBackendHandle());
        if (source.getBackendSampleOffset() != 0)
            AL11.alSourcei(handle, AL11.AL_SAMPLE_OFFSET, source.getBackendSampleOffset());
        applySourceState(source);

        source.setState(SoundState.Playing);
        AL11.alSourcePlay(handle);
    }

    @Override
    public void pauseSource(SoundSource source) {
        source.setState(SoundState.Paused);
        onCloseSource(source);
    }

    @Override
    public void stopSource(SoundSource source) {
        source.setBackendSampleOffset(0);
        source.setState(SoundState.Stopped);
        onCloseSource(source);
    }

    @Override
    public void rewindSource(SoundSource source) {
        source.setBackendSampleOffset(0);

        if (this.allocatedSources.contains(source))
            AL11.alSourceRewind(source.getBackendHandle());
    }

    @Override
    public void onChangeBuffer(SoundSource source) {
        stopSource(source);
    }

    private void allocateBuffer(SoundBuffer buffer) {
        if (buffer.getBackendHandle() == -1) {
            int handle = AL11.alGenBuffers();
            if (handle == 0)
                throw new IllegalStateException("Failed allocate buffer");
            AL11.alBufferData(handle, soundFormatToAL(buffer.getSoundFormat()), buffer.getPcm(), buffer.getSampleRate());
            buffer.setBackendHandle(handle);
            this.activeBuffers.add(handle);
        }
    }

    @Override
    public void onCloseBuffer(SoundBuffer buffer) {
        int handle = buffer.getBackendHandle();
        if (handle != -1 && this.activeBuffers.contains(handle)) {
            AL11.alDeleteBuffers(handle);
            buffer.setBackendHandle(-1);
            this.activeBuffers.remove(handle);
        }
    }

    @Override
    public void onCloseSource(SoundSource source) {
        int handle = source.getBackendHandle();

        if (this.allocatedSources.contains(source)) {
            AL11.alSourceStop(handle);
            this.sourcePool.release(handle);
            source.setBackendHandle(-1);
            this.allocatedSources.remove(source);
        }
    }

    @Override
    public void close() {
        for (SoundBuffer buffer : Overtone.getBuffers())
            onCloseBuffer(buffer);

        for (SoundSource source : Overtone.getSources()) {
            //Сохранение позиции воспроизведения
            if (source.getState() == SoundState.Playing) {
                int handle = source.getBackendHandle();

                if (handle == -1)
                    System.err.println("Synchronization error: source is playing but is not allocated!");
                else
                    source.setBackendSampleOffset(AL11.alGetSourcei(source.getBackendHandle(), AL11.AL_SAMPLE_OFFSET));
            }

            onCloseSource(source);
        }

        MemoryUtil.memFree(this.sourceRotationCacheBuffer);

        this.sourcePool.close();
        ALC11.alcMakeContextCurrent(MemoryUtil.NULL);
        ALC11.alcDestroyContext(this.context);
    }

    public static int soundFormatToAL(SoundFormat soundFormat) {
        return switch (soundFormat) {
            case Mono -> AL11.AL_FORMAT_MONO16;
            case Stereo -> AL11.AL_FORMAT_STEREO16;
        };
    }
}
