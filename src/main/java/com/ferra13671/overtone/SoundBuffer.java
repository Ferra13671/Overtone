package com.ferra13671.overtone;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.lwjgl.system.MemoryUtil;

import java.nio.ShortBuffer;

@Getter
public class SoundBuffer implements AutoCloseable {
    public static final SoundBuffer EMPTY = new SoundBuffer(SoundFormat.Mono, null, 0);

    private final SoundFormat soundFormat;
    private ShortBuffer pcm;
    private final int sampleRate;

    @Getter(AccessLevel.PACKAGE)
    @Setter(AccessLevel.PACKAGE)
    private int backendHandle = -1;

    SoundBuffer(SoundFormat soundFormat, ShortBuffer pcm, int sampleRate) {
        this.soundFormat = soundFormat;
        this.pcm = pcm == null ? null : MemoryUtil.memAllocShort(pcm.remaining()).put(pcm).flip();
        this.sampleRate = sampleRate;
    }

    @Override
    public void close() {
        if (this.pcm != null) {
            MemoryUtil.memFree(this.pcm);
            this.pcm = null;
        }

        Overtone.closeBuffer(this);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof SoundBuffer buffer && (this == buffer || (
                this.soundFormat == buffer.soundFormat &&
                this.pcm == buffer.pcm &&
                this.sampleRate == buffer.sampleRate
        ));
    }
}
