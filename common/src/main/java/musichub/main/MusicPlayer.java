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

    private int volume = 100;

    private BlockingQueue<String> elementsToPlay;
    private SourceDataLine currentDataLine;

    public MusicPlayer(IMusicHub hub) {
        this.hub = hub;
        this.elementsToPlay = new ArrayBlockingQueue<>(100);

        new Thread(this::musicLoop).start();
    }

    public void queueMusic(String musicName) {
        this.elementsToPlay.add(musicName);
        System.out.println(musicName + " queued!");
    }

    public void setVolume(int volume) {
        if (volume < 0)
            volume = 0;
        if (volume > 100)
            volume = 100;

        this.volume = volume;

        if (this.currentDataLine != null)
            dataLineSetVolume();
    }

    private void dataLineSetVolume() {
        final FloatControl volumeControl = (FloatControl) currentDataLine.getControl( FloatControl.Type.MASTER_GAIN );
        volumeControl.setValue(20.0f * (float) Math.log10(volume / 100.0f));
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
            currentDataLine = (SourceDataLine) AudioSystem.getLine(info);

            currentDataLine.open(audioFormat);
            currentDataLine.start();

            this.dataLineSetVolume();

            int nBytesRead = 0;
            byte[] abData = new byte[BUFFER_SIZE];
            while (nBytesRead != -1) {
                nBytesRead = audioStream.read(abData, 0, abData.length);

                if (nBytesRead >= 0) {
                    currentDataLine.write(abData, 0, nBytesRead);
                }
            }

            currentDataLine.drain();
            currentDataLine.close();
        } catch (NoElementFoundException | IOException | UnsupportedAudioFileException | LineUnavailableException e) {
            e.printStackTrace();
        }

        currentDataLine = null;
    }
}
