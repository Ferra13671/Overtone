import com.ferra13671.overtone.Overtone;
import com.ferra13671.overtone.PCMGenerator;
import com.ferra13671.overtone.api.SoundBuffer;
import com.ferra13671.overtone.api.SoundFormat;
import com.ferra13671.overtone.api.SoundSource;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Main {

    public void main(String[] args) {
        Overtone.init();

        try {
            SoundBuffer buffer = Overtone.createSoundBuffer(
                    PCMGenerator.brownNoise(3000, Overtone.DEFAULT_SAMPLE_RATE),
                    SoundFormat.Mono,
                    Overtone.DEFAULT_SAMPLE_RATE
            );

            try(SoundSource source = Overtone.getBackend().getActiveScene().createSource()) {
                source.setBuffer(buffer);
                source.setVolume(0.1f);
                source.setLooping(true);
                source.play();

                //Overtone.getBackend().getActiveScene().getListener().setPosition(new Vector3f(
                //        -5f, 0f, 0f
                //));

                source.awaitPlaybackCompletion();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Overtone.close();
    }
}
