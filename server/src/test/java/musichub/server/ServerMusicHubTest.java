package musichub.server;

import musichub.business.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Date;

public class ServerMusicHubTest {
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

    public ServerMusicHubTest() {
        this.alb1.addSong(sg1.getUuid());
        this.alb1.addSong(sg2.getUuid());
        this.alb2.addSong(sg3.getUuid());

        this.pl1.addElement(sg2.getUuid());
        this.pl1.addElement(b1.getUuid());

        this.pl2.addElement(sg1.getUuid());
        this.pl2.addElement(sg3.getUuid());
    }

    @BeforeEach
    public void initTest() {
        hub = new ServerMusicHub();

        hub.addElement(sg1);
        hub.addElement(sg2);
        hub.addElement(sg3);
        hub.addElement(b1);
        hub.addElement(b2);

        hub.addAlbum(alb1);
        hub.addAlbum(alb2);

        hub.addPlaylist(pl1);
        hub.addPlaylist(pl2);
    }

    // Right

    @Test
    public void testCorrectElementList() {
        Assertions.assertArrayEquals(new AudioElement[] {sg1, sg2, sg3, b1, b2}, hub.elements());
    }

    @Test
    public void testCorrectSongList() {
        Assertions.assertArrayEquals(new Song[] {sg1, sg2, sg3}, hub.songs());
    }

    @Test
    public void testCorrectAudioBooksList() {
        Assertions.assertArrayEquals(new AudioBook[] {b1, b2}, hub.audioBooks());
    }

    @Test
    public void testCorrectAudioBooksByAuthorList() {
        Assertions.assertArrayEquals(new AudioBook[] {b2, b1}, hub.getAudiobooksSortedByAuthor());
    }

    @Test
    public void testCorrectAlbumList() {
        Assertions.assertArrayEquals(new Album[] { alb1, alb2 }, hub.albums());
    }

    @Test
    public void testCorrectAlbumByDateList() {
        Assertions.assertArrayEquals(new Album[] { alb2, alb1 }, hub.getAlbumsSortedByDate());
    }

    @Test
    public void testCorrectPlaylistList() {
        Assertions.assertArrayEquals(new PlayList[] { pl1, pl2 }, hub.playlists());
    }

    @Test
    public void testFindElement() throws NoElementFoundException {
        Assertions.assertEquals(sg1, hub.elementByTitle(sg1.getTitle()));
        Assertions.assertEquals(sg2, hub.elementByTitle(sg2.getTitle()));
        Assertions.assertEquals(sg3, hub.elementByTitle(sg3.getTitle()));
        Assertions.assertEquals(b1, hub.elementByTitle(b1.getTitle()));
        Assertions.assertEquals(b2, hub.elementByTitle(b2.getTitle()));
    }

    @Test
    public void testFindElementById() throws NoElementFoundException {
        Assertions.assertEquals(sg1, hub.elementById(sg1.getUuid()));
        Assertions.assertEquals(sg2, hub.elementById(sg2.getUuid()));
        Assertions.assertEquals(sg3, hub.elementById(sg3.getUuid()));
        Assertions.assertEquals(b1, hub.elementById(b1.getUuid()));
        Assertions.assertEquals(b2, hub.elementById(b2.getUuid()));
    }

    @Test
    public void testFindAlbum() throws NoAlbumFoundException {
        Assertions.assertEquals(alb1, hub.albumByTitle(alb1.getTitle()));
        Assertions.assertEquals(alb2, hub.albumByTitle(alb2.getTitle()));
    }

    @Test
    public void testFindPlaylist() throws NoPlayListFoundException {
        Assertions.assertEquals(pl1, hub.playlistByTitle(pl1.getTitle()));
        Assertions.assertEquals(pl2, hub.playlistByTitle(pl2.getTitle()));
    }

    @Test
    public void testFindAlbumSongs() throws NoAlbumFoundException, NoElementFoundException {
        Assertions.assertArrayEquals(new Song[] { sg1, sg2 }, hub.getAlbumSongs(alb1.getTitle()));
    }

    @Test
    public void testFindPlaylistElements() throws NoElementFoundException, NoPlayListFoundException {
        Assertions.assertArrayEquals(new AudioElement[] { sg2, b1 }, hub.getPlaylistElements("Playlist1"));
    }

    @Test
    public void testAddSongToAlbum() throws NoAlbumFoundException, NoElementFoundException {
        hub.addAlbum(new Album("Album3", "ArtistX", 2031, new Date()));
        hub.addElementToAlbum("Song1", "Album3");
        hub.addElementToAlbum("Song3", "Album3");

        Assertions.assertArrayEquals(new Song[] { sg1, sg3 }, hub.getAlbumSongs("Album3"));
    }

    @Test
    public void testAddElementToPlaylist() throws NoElementFoundException, NoPlayListFoundException {
        hub.addPlaylist(new PlayList("Playlist3"));
        hub.addElementToPlayList("Song1", "Playlist3");
        hub.addElementToPlayList("Song3", "Playlist3");

        Assertions.assertArrayEquals(new AudioElement[] { sg1, sg3 }, hub.getPlaylistElements("Playlist3"));
    }

    @Test
    public void testFindAlbumSongsByGenre() throws NoAlbumFoundException, NoElementFoundException {
        Assertions.assertArrayEquals(new Song[] { sg2, sg1 }, hub.getAlbumSongsSortedByGenre(alb1.getTitle()));
    }

    @Test
    public void testDownloadElement() throws NoAlbumFoundException, NoElementFoundException {
        Assertions.assertEquals(sg1.getContent(), hub.downloadElement(sg1.getTitle()).getName());
    }

    @Test
    public void testDeleteInsertPlaylist() throws NoPlayListFoundException {
        hub.deletePlayList(pl1.getTitle());
        Assertions.assertThrows(NoPlayListFoundException.class, () -> hub.playlistByTitle(pl1.getTitle()));
        hub.addPlaylist(pl1);
        Assertions.assertEquals(pl1, hub.playlistByTitle(pl1.getTitle()));
    }

    @Test
    public void testDeleteInsertAlbum() throws NoAlbumFoundException {
        hub.deleteAlbum(alb1.getTitle());
        Assertions.assertThrows(NoAlbumFoundException.class, () -> hub.albumByTitle(alb1.getTitle()));
        hub.addAlbum(alb1);
        Assertions.assertEquals(alb1, hub.albumByTitle(alb1.getTitle()));
    }

    @Test
    public void testDeleteInserElement() throws NoElementFoundException {
        hub.deleteElement(b1.getTitle());
        Assertions.assertThrows(NoElementFoundException.class, () -> hub.elementByTitle(b1.getTitle()));
        hub.addElement(b1);
        Assertions.assertEquals(b1, hub.elementByTitle(b1.getTitle()));
    }

    // Boundary
    @Test
    public void testFindNull() throws NoElementFoundException {
        Assertions.assertThrows(NullPointerException.class, () -> hub.albumByTitle(null));
        Assertions.assertThrows(NullPointerException.class, () -> hub.elementByTitle(null));
        Assertions.assertThrows(NullPointerException.class, () -> hub.playlistByTitle(null));
        Assertions.assertThrows(NullPointerException.class, () -> hub.elementById(null));
    }

    @Test
    public void testFindAlbumSongsFailIfBook() {
        Album alb = new Album("testAlb", "Artist10", 100, new Date());
        alb.addSong(b1.getUuid()); // not a song

        hub.addAlbum(alb);
        Assertions.assertThrows(NoElementFoundException.class, () -> hub.getAlbumSongs("testAlb"));
    }

    // Error-condition
    @Test
    public void testDownloadElementIOException() throws NoElementFoundException {
        Assertions.assertThrows(IOException.class, () -> hub.downloadElement(sg1.getTitle()).getInputStream());
    }

    @Test
    public void testFindElementError() {
        Assertions.assertThrows(NoElementFoundException.class, () -> hub.elementByTitle(alb1.getTitle()));
        Assertions.assertThrows(NoElementFoundException.class, () -> hub.elementByTitle(pl1.getTitle()));
    }

    @Test
    public void testFindAlbumError() {
        Assertions.assertThrows(NoPlayListFoundException.class, () -> hub.playlistByTitle(alb1.getTitle()));
        Assertions.assertThrows(NoPlayListFoundException.class, () -> hub.playlistByTitle(sg1.getTitle()));
    }

    @Test
    public void testFindPlaylistError() {
        Assertions.assertThrows(NoPlayListFoundException.class, () -> hub.playlistByTitle(alb1.getTitle()));
        Assertions.assertThrows(NoPlayListFoundException.class, () -> hub.playlistByTitle(sg1.getTitle()));
    }

    @Test
    public void testAddAudiobookToAlbumFail() throws NoAlbumFoundException, NoElementFoundException {
        hub.addAlbum(new Album("Album3", "ArtistX", 2031, new Date()));
        Assertions.assertThrows(NoElementFoundException.class, () -> hub.addElementToAlbum("Book1", "Album3"));
    }

    // Performance
}
