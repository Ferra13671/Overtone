package com.ferra13671.overtone.api;

import com.ferra13671.overtone.HandleableLong;
import org.lwjgl.openal.ALCapabilities;

public interface Scene extends HandleableLong {

    void suspend();

    void process();

    SoundSource createSource();

    ALCapabilities getCapabilities();

    void close();
}
