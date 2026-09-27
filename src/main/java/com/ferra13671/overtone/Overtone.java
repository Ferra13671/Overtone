package com.ferra13671.overtone;

import com.ferra13671.overtone.api.Backend;
import com.ferra13671.overtone.api.SoundBuffer;
import com.ferra13671.overtone.api.SoundFormat;
import com.ferra13671.overtone.api.decoder.Decoder;
import com.ferra13671.overtone.api.decoder.DecodedAudio;
import com.ferra13671.overtone.impl.ALBackend;
import com.ferra13671.overtone.impl.decoder.OGGDecoder;
import com.ferra13671.overtone.impl.decoder.WAVDecoder;
import lombok.Getter;
import lombok.experimental.UtilityClass;

import java.io.InputStream;
import java.nio.ShortBuffer;

@UtilityClass
public class Overtone {
    public final int DEFAULT_SAMPLE_RATE = 44100;

    public final Decoder OGG_DECODER = new OGGDecoder();
    public final Decoder WAV_DECODER = new WAVDecoder();

    @Getter
    private Backend backend;

    public void init() {
        backend = new ALBackend();
    }

    public SoundBuffer createSoundBuffer(Decoder decoder, InputStream inputStream) {
        SoundBuffer soundBuffer = getBackend().createBuffer();

        try(DecodedAudio decodedAudio = decoder.decode(inputStream)) {
            soundBuffer.uploadData(
                    SoundFormat.forChannels(decodedAudio.channels()),
                    decodedAudio.pcm(),
                    decodedAudio.sampleRate()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        return soundBuffer;
    }

    public SoundBuffer createSoundBuffer(ShortBuffer pcm, SoundFormat format, int sampleRate) {
        SoundBuffer soundBuffer = getBackend().createBuffer();

        soundBuffer.uploadData(
                format,
                pcm,
                sampleRate
        );

        return soundBuffer;
    }

    public void close() {
        backend.close();
    }
}
