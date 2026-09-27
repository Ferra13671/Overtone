package com.ferra13671.overtone.api;

public interface AudioBackend {

    AudioDevice getDevice();

    AudioContext getCurrentContext();

    AudioBuffer createBuffer();

    AudioSource createSource();

    void close();
}
