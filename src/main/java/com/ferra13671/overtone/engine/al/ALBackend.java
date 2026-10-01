package com.ferra13671.overtone.engine.al;

import com.ferra13671.overtone.*;
import com.ferra13671.overtone.engine.Backend;
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

    public ALBackend(long deviceHandle, ALCCapabilities deviceCapabilities) {
        this.context = ALC11.alcCreateContext(deviceHandle, (IntBuffer) null);
        if (!ALC11.alcMakeContextCurrent(this.context))
            throw new IllegalStateException("Cannot make current context");
        this.contextCapabilities = AL.createCapabilities(deviceCapabilities);

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
            if (Internals.getBackendHandle(source) == -1) {
                System.err.println("Synchronization error: allocated SoundSource not loaded into OpenAL!");
                this.allocatedSources.remove(source);
                continue;
            }

            if (Internals.isDirty(source))
                applySourceState(source);

            if (AL11.alGetSourcei(Internals.getBackendHandle(source), AL11.AL_SOURCE_STATE) != AL11.AL_PLAYING)
                stopSource(source);
        }

        Listener listener = Overtone.getListener();
        if (Internals.isDirty(listener))
            applyListenerState(listener);
    }

    private void applySourceState(SoundSource source) {
        int handle = Internals.getBackendHandle(source);

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

        Internals.setDirty(source, false);
    }

    private void applyListenerState(Listener listener) {
        Vector3fc position = listener.getPosition();
        Vector3fc lookVector = listener.getLookVector();
        Vector3fc upVector = listener.getUpVector();

        AL11.alListener3f(AL11.AL_POSITION, position.x(), position.y(), position.z());
        this.sourceRotationCacheBuffer
                .put(0, lookVector.x()).put(1, lookVector.y()).put(2, lookVector.z())
                .put(3, upVector.x()).put(4, upVector.y()).put(5, upVector.z());
        AL11.alListenerfv(AL11.AL_ORIENTATION, this.sourceRotationCacheBuffer);
    }

    @Override
    public void playSource(SoundSource source) {
        if (source.getSoundBuffer() == null)
            return;

        int handle;
        if (Internals.getBackendHandle(source) == -1) {
            handle = this.sourcePool.acquire();
            this.allocatedSources.add(source);
            Internals.setBackendHandle(source, handle);
        } else
            handle = Internals.getBackendHandle(source);

        allocateBuffer(source.getSoundBuffer());

        AL11.alSourcei(handle, AL11.AL_BUFFER, Internals.getBackendHandle(source.getSoundBuffer()));
        if (Internals.getBackendSampleOffset(source) != 0) {
            AL11.alSourcei(handle, AL11.AL_SAMPLE_OFFSET, Internals.getBackendSampleOffset(source));
            Internals.setBackendSampleOffset(source, 0);
        }
        applySourceState(source);

        Internals.setState(source, SoundState.Playing);
        AL11.alSourcePlay(handle);
    }

    @Override
    public void pauseSource(SoundSource source) {
        Internals.setState(source, SoundState.Paused);
        Internals.setBackendSampleOffset(source, AL11.alGetSourcei(Internals.getBackendHandle(source), AL11.AL_SAMPLE_OFFSET));
        onCloseSource(source);
    }

    @Override
    public void stopSource(SoundSource source) {
        Internals.setBackendSampleOffset(source, 0);
        Internals.setState(source, SoundState.Stopped);
        onCloseSource(source);
    }

    @Override
    public void rewindSource(SoundSource source) {
        Internals.setBackendSampleOffset(source, 0);

        if (this.allocatedSources.contains(source))
            AL11.alSourceRewind(Internals.getBackendHandle(source));
    }

    @Override
    public void onChangeBuffer(SoundSource source) {
        stopSource(source);
    }

    private void allocateBuffer(SoundBuffer buffer) {
        if (Internals.getBackendHandle(buffer) == -1) {
            int handle = AL11.alGenBuffers();
            if (handle == 0)
                throw new IllegalStateException("Failed allocate buffer");
            AL11.alBufferData(handle, soundFormatToAL(buffer.getSoundFormat()), buffer.getPcm(), buffer.getSampleRate());
            Internals.setBackendHandle(buffer, handle);
            this.activeBuffers.add(handle);
        }
    }

    @Override
    public void onCloseBuffer(SoundBuffer buffer) {
        int handle = Internals.getBackendHandle(buffer);
        if (handle != -1 && this.activeBuffers.contains(handle)) {
            AL11.alDeleteBuffers(handle);
            Internals.setBackendHandle(buffer, -1);
            this.activeBuffers.remove(handle);
        }
    }

    @Override
    public void onCloseSource(SoundSource source) {
        int handle = Internals.getBackendHandle(source);

        if (this.allocatedSources.contains(source)) {
            AL11.alSourceStop(handle);
            this.sourcePool.release(handle);
            Internals.setBackendHandle(source, -1);
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
                int handle = Internals.getBackendHandle(source);

                if (handle == -1)
                    System.err.println("Synchronization error: source is playing but is not allocated!");
                else
                    Internals.setBackendSampleOffset(source, AL11.alGetSourcei(Internals.getBackendHandle(source), AL11.AL_SAMPLE_OFFSET));
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
