package com.ferra13671.overtone.impl.decoder;

import com.ferra13671.overtone.api.decoder.AudioDecoder;
import com.ferra13671.overtone.api.decoder.DecodedAudio;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public class OGGDecoder implements AudioDecoder {

    @Override
    public DecodedAudio decode(InputStream inputStream) throws Exception {
        byte[] bytes = inputStream.readAllBytes();
        ByteBuffer buffer = MemoryUtil.memAlloc(bytes.length);
        buffer.put(bytes).flip();

        try(MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer errorBuffer = stack.mallocInt(1);

            long decoder = STBVorbis.stb_vorbis_open_memory(buffer, errorBuffer, null);
            if (decoder == MemoryUtil.NULL)
                throw new IllegalStateException("Cannot load OGG sound.");

            STBVorbisInfo info = STBVorbisInfo.malloc(stack);
            STBVorbis.stb_vorbis_get_info(decoder, info);

            int channels = info.channels();
            int sampleRate = info.sample_rate();

            int samplesPerChannel = STBVorbis.stb_vorbis_stream_length_in_samples(decoder);
            ShortBuffer pcm = MemoryUtil.memAllocShort(samplesPerChannel * channels);

            int samplesRead = STBVorbis.stb_vorbis_get_samples_short_interleaved(decoder, channels, pcm);
            pcm.position(0);
            pcm.limit(samplesRead * channels);

            STBVorbis.stb_vorbis_close(decoder);

            return new DecodedAudio(pcm, channels, sampleRate);
        } finally {
            MemoryUtil.memFree(buffer);
        }
    }
}
