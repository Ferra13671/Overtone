import com.ferra13671.overtone.*;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SpatialSourceTest {

    public void main(String[] args) {
        Overtone.init();

        try {
            SoundBuffer buffer = Overtone.createBuffer(
                    SoundFormat.Mono,
                    PCMGenerator.brownNoise(5000, Overtone.DEFAULT_SAMPLE_RATE),
                    Overtone.DEFAULT_SAMPLE_RATE
            );

            try(SpatialSoundSource source = Overtone.create3DSource()) {
                source.setSoundBuffer(buffer);
                source.setVolume(0.3f);
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

    private void loop(SpatialSoundSource source) throws Exception {
        Overtone.tick();

        source.setPosition(-3f * (float) Math.sin(Math.toRadians(System.currentTimeMillis() / 3)), 0f, -3f * (float) Math.cos(Math.toRadians(System.currentTimeMillis() / 3)));

        Thread.sleep(20);
    }
}
