package com.ferra13671.overtone;

import lombok.experimental.UtilityClass;
import org.apiguardian.api.API;

@API(status = API.Status.INTERNAL)
@UtilityClass
public class Internals {

    public boolean isDirty(SoundSource soundSource) {
        return soundSource.isDirty();
    }

    public void setDirty(SoundSource soundSource, boolean dirty) {
        soundSource.setDirty(dirty);
    }

    public int getBackendHandle(SoundSource soundSource) {
        return soundSource.getBackendHandle();
    }

    public void setBackendHandle(SoundSource soundSource, int handle) {
        soundSource.setBackendHandle(handle);
    }

    public int getBackendSampleOffset(SoundSource soundSource) {
        return soundSource.getBackendSampleOffset();
    }

    public void setBackendSampleOffset(SoundSource soundSource, int offset) {
        soundSource.setBackendSampleOffset(offset);
    }

    public void setState(SoundSource soundSource, SoundState state) {
        soundSource.setState(state);
    }

    public boolean isDirty(Listener listener) {
        return listener.isDirty();
    }

    public void setDirty(Listener listener, boolean dirty) {
        listener.setDirty(dirty);
    }

    public int getBackendHandle(SoundBuffer soundBuffer) {
        return soundBuffer.getBackendHandle();
    }

    public void setBackendHandle(SoundBuffer soundBuffer, int handle) {
        soundBuffer.setBackendHandle(handle);
    }
}
