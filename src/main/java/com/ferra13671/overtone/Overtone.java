package com.ferra13671.overtone;

import com.ferra13671.overtone.api.AudioBackend;
import com.ferra13671.overtone.api.SoundBuffer;
import com.ferra13671.overtone.api.SoundFormat;
import com.ferra13671.overtone.api.decoder.AudioDecoder;
import com.ferra13671.overtone.api.decoder.DecodedAudio;
import com.ferra13671.overtone.impl.ALBackend;
import com.ferra13671.overtone.impl.decoder.OGGDecoder;
import com.ferra13671.overtone.impl.decoder.WAVDecoder;
import lombok.Getter;
import lombok.experimental.UtilityClass;

import java.io.InputStream;

@UtilityClass
public class Overtone {
    public static final AudioDecoder OGG_DECODER = new OGGDecoder();
    public static final AudioDecoder WAV_DECODER = new WAVDecoder();

    @Getter
    private AudioBackend backend;

    public void init() {
        backend = new ALBackend();
        backend.getDevice().createContext().makeCurrent();
    }

    public SoundBuffer createSoundBuffer(AudioDecoder decoder, InputStream inputStream) {
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

    public void close() {
        backend.close();
    }
}
