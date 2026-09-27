package com.ferra13671.overtone.api;

import org.lwjgl.openal.ALCCapabilities;

import java.util.Set;

public interface AudioDevice {

    ALCCapabilities getCapabilities();

    Set<AudioContext> getAllContexts();

    AudioContext createContext();
}
