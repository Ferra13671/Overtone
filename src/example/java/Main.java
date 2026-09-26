import com.ferra13671.overtone.Overtone;
import com.ferra13671.overtone.SoundBuffer;
import com.ferra13671.overtone.SoundSource;
import lombok.experimental.UtilityClass;

import java.io.InputStream;

@UtilityClass
public class Main {

    public void main(String[] args) {
        Overtone.init();

        try(InputStream inputStream = Main.class.getClassLoader().getResourceAsStream("a.ogg")) {
            SoundBuffer buffer = Overtone.loadOggSound(inputStream);
            SoundSource source = new SoundSource();

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
