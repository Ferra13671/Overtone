package com.ferra13671.overtone.impl.decoder;

import com.ferra13671.overtone.api.decoder.AudioDecoder;
import com.ferra13671.overtone.api.decoder.DecodedAudio;
import org.lwjgl.system.MemoryUtil;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class WAVDecoder implements AudioDecoder {

    @Override
    public DecodedAudio decode(InputStream inputStream) throws Exception {
        try(AudioInputStream ais = AudioSystem.getAudioInputStream(inputStream)) {
            AudioFormat sourceFormat = ais.getFormat();

            AudioFormat decodeFormat = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    sourceFormat.getSampleRate(),
                    16,
                    sourceFormat.getChannels(),
                    sourceFormat.getChannels() * 2,
                    sourceFormat.getSampleRate(),
                    false
            );

            try(AudioInputStream pcmStream = AudioSystem.getAudioInputStream(decodeFormat, ais)) {
                byte[] bytes = pcmStream.readAllBytes();

                ByteBuffer pcm = MemoryUtil.memAlloc(bytes.length).order(ByteOrder.LITTLE_ENDIAN);
                pcm.put(bytes).flip();

                return new DecodedAudio(
                        pcm.asShortBuffer(),
                        decodeFormat.getChannels(),
                        (int) decodeFormat.getSampleRate()
                );
            }
        }
    }
}
