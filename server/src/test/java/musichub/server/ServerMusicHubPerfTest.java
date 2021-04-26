package musichub.server;

import musichub.business.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerMusicHubPerfTest {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private Song sg1 = new Song("Song1", "Artist1", 120, "unknownFile1", Genre.POP);
    private Song sg2 = new Song("Song2", "Artist1", 130, "unknownFile2", Genre.ROCK);
    private Song sg3 = new Song("Song3", "Artist2", 340, "unknownFile3", Genre.CLASSIC);
    private AudioBook b1 = new AudioBook("Book1", "ZArtist3", 293, "unknownFile4", Language.GERMAN, Category.SPEECH);
    private AudioBook b2 = new AudioBook("Book2", "Artist4", 203, "unknownFile5", Language.FRENCH, Category.NOVEL);

    private Album alb1 = new Album("Album1", "Artist1", 250, new Date(2000));
    private Album alb2 = new Album("Album2", "Artist2", 340, new Date(1000));
    private PlayList pl1 = new PlayList("Playlist1");
    private PlayList pl2 = new PlayList("Playlist2");

    private ServerMusicHub hub;

    public ServerMusicHubPerfTest() {
        LOGGER.setLevel(Level.OFF);

        this.alb1.addSong(sg1.getUuid());
        this.alb1.addSong(sg2.getUuid());
        this.alb2.addSong(sg3.getUuid());

        this.pl1.addElement(sg2.getUuid());
        this.pl1.addElement(b1.getUuid());

        this.pl2.addElement(sg1.getUuid());
        this.pl2.addElement(sg3.getUuid());

        hub = new ServerMusicHub();

        hub.addElement(sg1, null);
        hub.addElement(sg2, null);
        hub.addElement(sg3, null);
        hub.addElement(b1, null);
        hub.addElement(b2, null);

        hub.addAlbum(alb1);
        hub.addAlbum(alb2);

        hub.addPlaylist(pl1);
        hub.addPlaylist(pl2);

        for (int i = 0; i < 100000; i++)
            hub.addElement(new Song("s" + i, "prolificArtist", 100, "f" + i, Genre.HIPHOP), null);
    }

    @BeforeEach
    public void initTest() {
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.MILLISECONDS)
    public void testGetElement() throws NoElementFoundException {
        Assertions.assertEquals(sg1, hub.elementByTitle(sg1.getTitle()));
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.MILLISECONDS)
    public void testGetAlbum() throws NoAlbumFoundException, NoElementFoundException {
        Assertions.assertArrayEquals(new Song[] { sg1, sg2 }, hub.getAlbumSongs(alb1.getTitle()));
    }
}
