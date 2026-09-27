package com.ferra13671.overtone.api;

public interface AudioBackend {

    AudioDevice getDevice();

    AudioContext getCurrentContext();

    SoundBuffer createBuffer();

    SoundSource createSource();

    void close();
}
