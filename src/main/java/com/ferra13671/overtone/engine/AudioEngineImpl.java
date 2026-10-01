package com.ferra13671.overtone.engine;

import com.ferra13671.overtone.*;
import com.ferra13671.overtone.engine.al.ALBackend;
import com.ferra13671.overtone.engine.al.ALDevice;
import org.lwjgl.openal.ALC11;
import org.lwjgl.openal.EXTDisconnect;

public class AudioEngineImpl implements AudioEngine {
    private ALDevice device;
    private Backend backend;

    public AudioEngineImpl() {
        recreateBackend();
    }

    @Override
    public void tick() {
        if (isDeviceLost())
            recreateBackend();

        if (this.backend != null)
            this.backend.tick();
    }

    private boolean isDeviceLost() {
        return device.getCapabilities().ALC_EXT_disconnect && ALC11.alcGetInteger(device.getHandle(), EXTDisconnect.ALC_CONNECTED) == ALC11.ALC_FALSE;
    }

    void recreateBackend() {
        try {
            ALDevice newDevice = new ALDevice(null);

            if (this.device != null)
                this.device.close();

            if (this.backend != null)
                this.backend.close();

            this.device = newDevice;
            this.backend = new ALBackend(device.getHandle(), device.getCapabilities());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void playSource(SoundSource source) {
        this.backend.playSource(source);
    }

    @Override
    public void pauseSource(SoundSource source) {
        this.backend.pauseSource(source);
    }

    @Override
    public void stopSource(SoundSource source) {
        this.backend.stopSource(source);
    }

    @Override
    public void rewindSource(SoundSource source) {
        this.backend.rewindSource(source);
    }

    @Override
    public void onChangeBuffer(SoundSource source) {
        this.backend.onChangeBuffer(source);
    }

    @Override
    public void onCloseBuffer(SoundBuffer buffer) {
        this.backend.onCloseBuffer(buffer);
    }

    @Override
    public void onCloseSource(SoundSource source) {
        this.backend.onCloseSource(source);
    }

    @Override
    public void close() {
        if (this.backend != null) {
            this.backend.close();
            this.backend = null;
        }

        if (this.device != null) {
            this.device.close();
            this.device = null;
        }
    }
}
