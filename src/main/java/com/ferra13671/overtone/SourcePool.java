package com.ferra13671.overtone;

import org.lwjgl.openal.AL11;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

public class SourcePool {
    private final Deque<Integer> free = new ArrayDeque<>();
    private final Set<Integer> inUse = new HashSet<>();
    private final Set<Integer> overflow = new HashSet<>();

    private boolean closed = false;

    public SourcePool(int size) {
        if (size < 0)
            throw new IllegalStateException("Size must be >= 0");

        for (int i = 0; i < size; i++)
            this.free.add(createSource());
    }

    public int acquire() {
        if (this.closed)
            throw new IllegalStateException("SourcePool closed");

        Integer handle = this.free.pollFirst();

        if (handle == null) {
            handle = createSource();

            this.overflow.add(handle);
        }

        this.inUse.add(handle);

        return handle;
    }

    public void release(int handle) {
        if (!this.inUse.remove(handle))
            return;

        if (this.overflow.remove(handle)) {
            AL11.alDeleteSources(handle);
            return;
        }

        this.free.addLast(handle);
    }

    private int createSource() {
        int handle = AL11.alGenSources();
        if (handle == 0)
            throw new IllegalStateException("Failed create OpenAL source");

        return handle;
    }

    public void close() {
        if (this.closed)
            return;


        this.free.forEach(AL11::alDeleteSources);
        this.inUse.forEach(AL11::alDeleteSources);
        this.overflow.forEach(AL11::alDeleteSources);
        this.free.clear();
        this.inUse.clear();
        this.overflow.clear();

        this.closed = true;
    }
}
