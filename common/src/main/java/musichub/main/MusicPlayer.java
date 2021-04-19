package musichub.main;

import lombok.AllArgsConstructor;
import musichub.business.IMusicHub;
import musichub.business.NoElementFoundException;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;

@AllArgsConstructor
public class MusicPlayer {
    private final int BUFFER_SIZE = 128000;
    private final IMusicHub hub;

    public void playMusic(String musicName) {
        try {
            BufferedInputStream is = new BufferedInputStream(this.hub.downloadElement(musicName).getInputStream());
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(is);

            AudioFormat audioFormat = audioStream.getFormat();

            DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
            SourceDataLine sourceLine = (SourceDataLine) AudioSystem.getLine(info);

            sourceLine.open(audioFormat);
            sourceLine.start();

            int nBytesRead = 0;
            byte[] abData = new byte[BUFFER_SIZE];
            while (nBytesRead != -1) {
                try {
                    nBytesRead = audioStream.read(abData, 0, abData.length);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (nBytesRead >= 0) {
                    sourceLine.write(abData, 0, nBytesRead);
                }
            }

            sourceLine.drain();
            sourceLine.close();
        } catch (NoElementFoundException | IOException | UnsupportedAudioFileException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
}
