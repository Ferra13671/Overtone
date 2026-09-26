package com.ferra13671.overtone;

import lombok.experimental.UtilityClass;
import org.lwjgl.openal.*;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

@UtilityClass
public class Overtone {

    private long device = -1L;
    private long context = -1L;

    public void init() {
        device = ALC10.alcOpenDevice((ByteBuffer) null);
        if (device == MemoryUtil.NULL)
            throw new IllegalStateException("Cannot open device.");

        ALCCapabilities alcCapabilities = ALC.createCapabilities(device);
        if (!alcCapabilities.OpenALC11)
            throw new IllegalStateException("Device doesn't support OpenALC11.");

        context = ALC11.alcCreateContext(device, (IntBuffer) null);
        if (context == MemoryUtil.NULL)
            throw new IllegalStateException("Cannot create context.");

        if (!ALC11.alcMakeContextCurrent(context))
            throw new IllegalStateException("Cannot activate context.");

        ALCapabilities capabilities = AL.createCapabilities(alcCapabilities);
        if (!capabilities.OpenAL11)
            throw new IllegalStateException("Device doesn't support OpenAL11.");
    }

    public SoundBuffer loadOggSound(InputStream inputStream) throws Exception {
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
            MemoryUtil.memFree(buffer);

            int bufferId = AL11.alGenBuffers();

            SoundFormat format = SoundFormat.forChannels(channels);
            AL11.alBufferData(bufferId, format.id, pcm, sampleRate);
            MemoryUtil.memFree(pcm);

            return new SoundBuffer(bufferId, format, sampleRate);
        }
    }

    public void close() {
        ALC11.alcMakeContextCurrent(MemoryUtil.NULL);
        ALC11.alcDestroyContext(context);
        ALC11.alcCloseDevice(device);
    }
}
