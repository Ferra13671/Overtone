package com.ferra13671.overtone;

import lombok.experimental.UtilityClass;
import org.lwjgl.system.MemoryUtil;

import java.nio.ShortBuffer;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Class for creating some primitive sounds or tests
 */
@UtilityClass
public class PCMGenerator {

    public ShortBuffer combine(ShortBuffer src, ShortBuffer dst, int dstOffset) {
        int srcLen = src.limit();
        int dstLen = dst.limit();

        int length = srcLen;
        if (dstLen > 0)
            length = Math.max(length, dstOffset + dstLen);

        ShortBuffer newBuffer = MemoryUtil.memAllocShort(length);

        for (int i = 0; i < length; i++) {
            int sum = i < srcLen ? src.get(i) : 0;

            int dstIndex = i - dstOffset;
            if (dstIndex >= 0 && dstIndex < dstLen)
                sum += dst.get(dstIndex);

            newBuffer.put(i, (short) sum);
        }

        return newBuffer;
    }

    public ShortBuffer sine(float freq, int durationMs, int sampleRate) {
        int samples = samplesFor(durationMs, sampleRate);
        ShortBuffer buf = MemoryUtil.memAllocShort(samples);
        double step = 2.0 * Math.PI * freq / sampleRate;
        for (int i = 0; i < samples; i++) {
            buf.put(i, (short) (Math.sin(step * i) * Short.MAX_VALUE));
        }
        return buf;
    }

    public ShortBuffer square(float freq, int durationMs, int sampleRate) {
        int samples = samplesFor(durationMs, sampleRate);
        ShortBuffer buf = MemoryUtil.memAllocShort(samples);
        double period = (double) sampleRate / freq;
        for (int i = 0; i < samples; i++) {
            boolean high = ((i % period) / period) < 0.5;
            buf.put(i, (high ? Short.MAX_VALUE : Short.MIN_VALUE));
        }
        return buf;
    }

    public ShortBuffer triangle(float freq, int durationMs, int sampleRate) {
        int samples = samplesFor(durationMs, sampleRate);
        ShortBuffer buf = MemoryUtil.memAllocShort(samples);
        double period = (double) sampleRate / freq;
        for (int i = 0; i < samples; i++) {
            double t = (i % period) / period;
            double v = t < 0.5 ? (4 * t - 1) : (3 - 4 * t);
            buf.put(i, (short) (v * Short.MAX_VALUE));
        }
        return buf;
    }

    public ShortBuffer sawtooth(float freq, int durationMs, int sampleRate) {
        int samples = samplesFor(durationMs, sampleRate);
        ShortBuffer buf = MemoryUtil.memAllocShort(samples);
        double period = (double) sampleRate / freq;
        for (int i = 0; i < samples; i++) {
            double t = (i % period) / period;
            buf.put(i, (short) ((2 * t - 1) * Short.MAX_VALUE));
        }
        return buf;
    }

    public static ShortBuffer pluck(float freq, int durationMs, int sampleRate) {
        int n = samplesFor(durationMs, sampleRate);
        ShortBuffer buf = MemoryUtil.memAllocShort(n);

        int delaySize = (int) (sampleRate / freq);
        float[] delay = new float[delaySize];
        for (int i = 0; i < delaySize; i++) {
            delay[i] = (float) (ThreadLocalRandom.current().nextDouble() * 2 - 1);
        }

        float damping = 0.996f;
        int idx = 0;
        for (int i = 0; i < n; i++) {
            float sample = delay[idx];
            float next = delay[(idx + 1) % delaySize];
            delay[idx] = (sample + next) * 0.5f * damping;
            idx = (idx + 1) % delaySize;
            buf.put(i, (short) (sample * Short.MAX_VALUE));
        }
        return buf;
    }

    public ShortBuffer whiteNoise(int durationMs, int sampleRate) {
        int samples = samplesFor(durationMs, sampleRate);
        ShortBuffer buffer = MemoryUtil.memAllocShort(samples);

        for (int i = 0; i < samples; i++) {
            int sample = ThreadLocalRandom.current().nextInt(Short.MAX_VALUE * 2 + 1) - Short.MAX_VALUE;
            buffer.put(i, (short) sample);
        }

        return buffer;
    }

    public ShortBuffer pinkNoise(int durationMs, int sampleRate) {
        int samples = samplesFor(durationMs, sampleRate);
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
        int samples = samplesFor(durationMs, sampleRate);
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
        int samples = samplesFor(durationMs, sampleRate);
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
        int samples = samplesFor(durationMs, sampleRate);
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

    private int samplesFor(int durationMs, int sampleRate) {
        return (sampleRate * durationMs) / 1000;
    }
}
