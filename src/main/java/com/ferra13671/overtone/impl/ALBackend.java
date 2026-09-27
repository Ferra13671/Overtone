package com.ferra13671.overtone.impl;

import com.ferra13671.overtone.api.AudioBackend;
import com.ferra13671.overtone.api.SoundBuffer;
import com.ferra13671.overtone.api.SoundSource;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public final class ALBackend implements AudioBackend {
    @Getter
    private final ALDevice device;
    @Getter
    @Setter(AccessLevel.PACKAGE)
    private ALContext currentContext;

    public ALBackend() {
        device = new ALDevice(null, this);
    }

    @Override
    public SoundBuffer createBuffer() {
        return new ALBuffer();
    }

    @Override
    public SoundSource createSource() {
        return new ALSource();
    }

    @Override
    public void close() {
        this.device.close();
    }
}
