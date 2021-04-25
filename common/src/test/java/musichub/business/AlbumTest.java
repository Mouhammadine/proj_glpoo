package musichub.business;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class AlbumTest {
    private Album album;

    @BeforeEach
    public void setUp() throws Exception {
        album = new Album("Example title",  "Example artist", 300, new Date());
    }

    @Test
    public void testGetters() {
        Assertions.assertEquals("Example title", album.getTitle());
        Assertions.assertEquals("Example artist", album.getArtist());
        Assertions.assertEquals(300, album.getLengthInSeconds());
    }

    @Test
    public void testInsertSongs() {
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();

        album.addSong(uuid1);
        album.addSong(uuid2);

        Assertions.assertEquals(Arrays.asList(uuid1, uuid2), album.getSongs());
    }

    @Test
    public void testRandomSongs() {
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();

        album.addSong(uuid1);
        album.addSong(uuid2);
        List<UUID> ids = album.getSongsRandomly();

        Assertions.assertTrue((uuid1 == ids.get(0) && uuid2 == ids.get(1)) || (uuid2 == ids.get(0) && uuid1 == ids.get(1)));
    }

    @Test
    public void testBoundaryNullSong() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            album.addSong(null);
        });
    }

    @Test
    public void testBoundaryInvalidLength() {
        Assertions.assertThrows(AssertionError.class, () -> {
            new Album("", "", -100, new Date());
        });
    }
}
