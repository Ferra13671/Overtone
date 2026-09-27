package com.ferra13671.overtone.api;

import lombok.AllArgsConstructor;
import org.lwjgl.openal.AL11;

@AllArgsConstructor
public enum SoundFormat {
    Mono(AL11.AL_FORMAT_MONO16),
    Stereo(AL11.AL_FORMAT_STEREO16);

    public final int id;

    public static SoundFormat forChannels(int channels) {
        return switch (channels) {
            case 1 -> Mono;
            case 2 -> Stereo;
            default -> throw new IllegalStateException("Overtone doesn't support 3 and more channels format.");
        };
    }
}
