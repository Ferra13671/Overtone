package com.ferra13671.overtone.decoder;

import java.io.InputStream;

public interface Decoder {

    DecodedAudio decode(InputStream inputStream) throws Exception;
}
