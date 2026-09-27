package com.ferra13671.overtone.api.decoder;

import java.io.InputStream;

public interface AudioDecoder {

    DecodedAudio decode(InputStream inputStream) throws Exception;
}
