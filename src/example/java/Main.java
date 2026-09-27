import com.ferra13671.overtone.Overtone;
import com.ferra13671.overtone.api.SoundBuffer;
import com.ferra13671.overtone.api.SoundSource;
import lombok.experimental.UtilityClass;

import java.io.InputStream;

@UtilityClass
public class Main {

    public void main(String[] args) {
        Overtone.init();
        Overtone.getBackend().getDevice().createContext().makeCurrent();

        try(InputStream inputStream = Main.class.getClassLoader().getResourceAsStream("a.ogg")) {
            SoundBuffer buffer = Overtone.createSoundBuffer(Overtone.OGG_DECODER, inputStream);
            SoundSource source = Overtone.getBackend().createSource();

            source.setBuffer(buffer);
            source.play();

            while (source.isPlaying()) {
                Thread.onSpinWait();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Overtone.close();
    }
}
