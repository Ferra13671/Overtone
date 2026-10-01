package com.ferra13671.overtone;

import com.ferra13671.overtone.decoder.Decoder;
import com.ferra13671.overtone.decoder.DecodedAudio;
import com.ferra13671.overtone.decoder.OGGDecoder;
import com.ferra13671.overtone.decoder.WAVDecoder;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.lwjgl.openal.ALC11;
import org.lwjgl.openal.EXTDisconnect;

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

    private volatile ALDevice device;
    @Getter
    private volatile Backend backend;

    public void init() {
        if (backend != null)
            throw new IllegalStateException("Overtone already initialized");

        device = new ALDevice(null);
        backend = new ALBackend(device);
    }

    public void tick() {
        if (isDeviceLost())
            recreateDevice();

        if (backend != null)
            backend.tick();
    }

    private boolean isDeviceLost() {
        return device.getCapabilities().ALC_EXT_disconnect && ALC11.alcGetInteger(device.getHandle(), EXTDisconnect.ALC_CONNECTED) == ALC11.ALC_FALSE;
    }

    private void recreateDevice() {
        try {
            ALDevice newDevice = new ALDevice(null);

            device.close();
            backend.close();
            device = newDevice;
            backend = new ALBackend(newDevice);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            backend.onCloseBuffer(soundBuffer);
            buffers.remove(soundBuffer);
        }
    }

    void closeSource(SoundSource soundSource) {
        if (sources.contains(soundSource)) {
            soundSource.setState(SoundState.Stopped);
            backend.onCloseSource(soundSource);
            sources.remove(soundSource);
        }
    }

    public void close() {
        if (backend == null)
            return;

        buffers.forEach(SoundBuffer::close);
        sources.forEach(SoundSource::close);

        backend.close();
        backend = null;
        device.close();
        device = null;
    }
}
