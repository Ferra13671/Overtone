package com.ferra13671.overtone.api;

public interface AudioContext extends AutoCloseable {

    AudioDevice getDevice();

    void suspend();

    void process();

    void makeCurrent();

    @Override
    void close();
}
