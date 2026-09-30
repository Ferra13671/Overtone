import com.ferra13671.overtone.*;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Main {

    public void main(String[] args) {
        Overtone.init();

        try {
            SoundBuffer buffer = Overtone.createBuffer(Overtone.OGG_DECODER, Main.class.getClassLoader().getResourceAsStream("a.ogg"));

            try(SoundSource source = Overtone.createSource()) {
                source.setSoundBuffer(buffer);
                source.setVolume(0.1f);
                source.setLooping(true);
                source.play();

                do {
                    loop(source);
                } while (source.getState() == SoundState.Playing);

                //Overtone.getBackend().getActiveScene().getListener().setPosition(new Vector3f(
                //        -5f, 0f, 0f
                //));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Overtone.close();
    }

    //for tests
    private void loop(SoundSource source) throws Exception {
        Overtone.tick();

        Thread.sleep(20);
    }
}
