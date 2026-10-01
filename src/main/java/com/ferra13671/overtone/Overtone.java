package com.ferra13671.overtone;

import com.ferra13671.overtone.decoder.Decoder;
import com.ferra13671.overtone.decoder.DecodedAudio;
import com.ferra13671.overtone.decoder.OGGDecoder;
import com.ferra13671.overtone.decoder.WAVDecoder;
import com.ferra13671.overtone.engine.AudioEngineImpl;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.UtilityClass;

import java.io.InputStream;
import java.nio.ShortBuffer;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@UtilityClass
public class Overtone {
    public final int DEFAULT_SAMPLE_RATE = 44100;

    public final Decoder OGG_DECODER = new OGGDecoder();
    public final Decoder WAV_DECODER = new WAVDecoder();

    private final List<SoundBuffer> buffers = new CopyOnWriteArrayList<>();
    private final List<SoundSource> sources = new CopyOnWriteArrayList<>();
    @Getter
    private final Listener listener = new Listener();

    @Getter(AccessLevel.PACKAGE)
    private AudioEngine engine;

    public void init() {
        init(new AudioEngineImpl());
    }

    public void init(AudioEngine audioEngine) {
        if (engine != null)
            throw new IllegalStateException("Overtone already initialized");

        engine = audioEngine;
    }

    public void tick() {
        engine.tick();
    }

    public List<SoundBuffer> getBuffers() {
        return Collections.unmodifiableList(buffers);
    }

    public List<SoundSource> getSources() {
        return Collections.unmodifiableList(sources);
    }

    public SoundBuffer createBuffer(Decoder decoder, InputStream inputStream) {
        try(DecodedAudio decodedAudio = decoder.decode(inputStream)) {
            return createBuffer(
                    SoundFormat.forChannels(decodedAudio.channels()),
                    decodedAudio.pcm(),
                    decodedAudio.sampleRate()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        return SoundBuffer.EMPTY;
    }

    public SoundBuffer createBuffer(SoundFormat format, ShortBuffer pcm, int sampleRate) {
        SoundBuffer soundBuffer = new SoundBuffer(format, pcm, sampleRate);

        buffers.add(soundBuffer);

        return soundBuffer;
    }

    public SpatialSoundSource create3DSource() {
        SpatialSoundSource soundSource = new SpatialSoundSource();

        sources.add(soundSource);

        return soundSource;
    }

    public SoundSource create2DSource() {
        SoundSource soundSource = new SoundSource();

        sources.add(soundSource);

        return soundSource;
    }

    void closeBuffer(SoundBuffer soundBuffer) {
        if (buffers.contains(soundBuffer)) {
            engine.onCloseBuffer(soundBuffer);
            buffers.remove(soundBuffer);
        }
    }

    void closeSource(SoundSource soundSource) {
        if (sources.contains(soundSource)) {
            soundSource.setState(SoundState.Stopped);
            engine.onCloseSource(soundSource);
            sources.remove(soundSource);
        }
    }

    public void close() {
        if (engine == null)
            return;

        buffers.forEach(SoundBuffer::close);
        sources.forEach(SoundSource::close);

        engine.close();
        engine = null;
    }
}
