package com.ferra13671.overtone;

public enum SoundFormat {
    Mono,
    Stereo;

    public static SoundFormat forChannels(int channels) {
        return switch (channels) {
            case 1 -> Mono;
            case 2 -> Stereo;
            default -> throw new IllegalStateException("Overtone doesn't support 3 and more channels format.");
        };
    }
}
