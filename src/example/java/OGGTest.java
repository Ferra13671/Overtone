import com.ferra13671.overtone.*;
import lombok.experimental.UtilityClass;

@UtilityClass
public class OGGTest {

    public void main(String[] args) {
        Overtone.init();

        try {
            SoundBuffer buffer = Overtone.createBuffer(Overtone.OGG_DECODER, OGGTest.class.getClassLoader().getResourceAsStream("test.ogg"));

            try(SoundSource source = Overtone.create2DSource()) {
                source.setSoundBuffer(buffer);
                source.setVolume(1f);
                source.setLooping(true);
                source.play();

                do {
                    loop(source);
                } while (source.getState() == SoundState.Playing);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Overtone.close();
    }

    private void loop(SoundSource source) throws Exception {
        Overtone.tick();

        Thread.sleep(20);
    }
}
