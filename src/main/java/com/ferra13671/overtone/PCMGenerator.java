package com.ferra13671.overtone;

import lombok.experimental.UtilityClass;
import org.lwjgl.system.MemoryUtil;

import java.nio.ShortBuffer;
import java.util.concurrent.ThreadLocalRandom;

@UtilityClass
public class PCMGenerator {

    public ShortBuffer whiteNoise(int durationMs, int sampleRate) {
        int samples = (sampleRate * durationMs) / 1000;
        ShortBuffer buffer = MemoryUtil.memAllocShort(samples);

        for (int i = 0; i < samples; i++) {
            int sample = ThreadLocalRandom.current().nextInt(Short.MAX_VALUE * 2 + 1) - Short.MAX_VALUE;
            buffer.put(i, (short) sample);
        }

        return buffer;
    }

    public ShortBuffer pinkNoise(int durationMs, int sampleRate) {
        int samples = (sampleRate * durationMs) / 1000;
        ShortBuffer buffer = MemoryUtil.memAllocShort(samples);

        float b0 = 0, b1 = 0, b2 = 0, b3 = 0, b4 = 0, b5 = 0, b6 = 0;

        for (int i = 0; i < samples; i++) {
            float white = ThreadLocalRandom.current().nextFloat() * 2f - 1f;   // [-1, 1]

            b0 = 0.99886f * b0 + white * 0.0555179f;
            b1 = 0.99332f * b1 + white * 0.0750759f;
            b2 = 0.96900f * b2 + white * 0.1538520f;
            b3 = 0.86650f * b3 + white * 0.3104856f;
            b4 = 0.55000f * b4 + white * 0.5329522f;
            b5 = -0.7616f * b5 - white * 0.0168980f;

            float pink = b0 + b1 + b2 + b3 + b4 + b5 + b6 + white * 0.5362f;
            b6 = white * 0.115926f;

            short sample = (short) (pink * 0.11f * Short.MAX_VALUE);
            buffer.put(i, sample);
        }

        return crossfadeLoop(buffer, sampleRate / 1000);
    }

    public ShortBuffer brownNoise(int durationMs, int sampleRate) {
        int samples = (sampleRate * durationMs) / 1000;
        ShortBuffer buffer = MemoryUtil.memAllocShort(samples);

        float last = 0f;

        for (int i = 0; i < samples; i++) {
            float white = ThreadLocalRandom.current().nextFloat() * 2f - 1f;
            last += white * 0.02f;

            if (last > 1f)  last =  2f - last;
            if (last < -1f) last = -2f - last;

            buffer.put(i, (short) (last * Short.MAX_VALUE));
        }

        return crossfadeLoop(buffer, sampleRate / 1000);
    }

    public ShortBuffer blueNoise(int durationMs, int sampleRate) {
        int samples = (sampleRate * durationMs) / 1000;
        ShortBuffer buffer = MemoryUtil.memAllocShort(samples);

        float prev = 0f;

        for (int i = 0; i < samples; i++) {
            float white = (float) (ThreadLocalRandom.current().nextDouble() * 2.0 - 1.0);
            float blue = white - prev;
            prev = white;

            int sample = (int) (blue * 0.5f * Short.MAX_VALUE);
            buffer.put(i, (short) Math.clamp(sample, Short.MIN_VALUE, Short.MAX_VALUE));
        }

        return buffer;
    }

    public ShortBuffer violetNoise(int durationMs, int sampleRate) {
        int samples = (sampleRate * durationMs) / 1000;
        ShortBuffer buffer = MemoryUtil.memAllocShort(samples);

        float prev1 = 0f, prev2 = 0f;

        for (int i = 0; i < samples; i++) {
            float white = (float) (ThreadLocalRandom.current().nextDouble() * 2.0 - 1.0);
            float violet = white - 2f * prev1 + prev2;   // вторая разность
            prev2 = prev1;
            prev1 = white;

            int sample = (int) (violet * 0.25f * Short.MAX_VALUE);
            buffer.put(i, (short) Math.clamp(sample, Short.MIN_VALUE, Short.MAX_VALUE));
        }

        return buffer;
    }

    public ShortBuffer crossfadeLoop(ShortBuffer source, int fadeSamples) {
        int total = source.limit();
        ShortBuffer result = MemoryUtil.memAllocShort(total);

        for (int i = 0; i < total; i++) {
            result.put(i, source.get(i));
        }

        for (int i = 0; i < fadeSamples; i++) {
            float t = (float) i / fadeSamples;
            short a = source.get(total - fadeSamples + i);
            short b = source.get(i);
            short mixed = (short) (a * (1 - t) + b * t);
            result.put(total - fadeSamples + i, mixed);
        }

        return result;
    }
}
