package musichub.main;

import musichub.business.IMusicHub;
import musichub.business.NoElementFoundException;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class MusicPlayer {
    private final int BUFFER_SIZE = 128000;
    private final IMusicHub hub;

    private BlockingQueue<String> elementsToPlay;

    public MusicPlayer(IMusicHub hub) {
        this.hub = hub;
        this.elementsToPlay = new ArrayBlockingQueue<>(100);

        new Thread(this::musicLoop).start();
    }

    public void queueMusic(String musicName) {
        this.elementsToPlay.add(musicName);
        System.out.println(musicName + " queued!");
    }

    private void musicLoop() {
        try {
            while (true) {
                this.playMusic(this.elementsToPlay.take());
            }
        } catch (InterruptedException ignored) { }
    }

    private void playMusic(String musicName) {
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
                nBytesRead = audioStream.read(abData, 0, abData.length);

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
