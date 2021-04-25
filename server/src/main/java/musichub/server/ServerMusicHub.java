package musichub.server;

import lombok.NonNull;
import musichub.business.*;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.File;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

class SortByDate implements Comparator<Album>
{
	public int compare(Album a1, Album a2) {
			return a1.getDate().compareTo(a2.getDate());
	} 
}

class SortByGenre implements Comparator<Song>
{
	public int compare(Song s1, Song s2) {
			return s1.getGenre().compareTo(s2.getGenre());
	} 
}

class SortByAuthor implements Comparator<AudioElement>
{
	public int compare(AudioElement e1, AudioElement e2) {
			return e1.getArtist().compareTo(e2.getArtist());
	} 
}

/**
 * Implemention of music hub for server
 */
@XmlRootElement(name = "musichub")
@WebService(endpointInterface = "musichub.business.IMusicHub")
public class ServerMusicHub implements IMusicHub {
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	static final String DIR = System.getProperty("user.dir");
	static final String FILE_PATH = DIR + "musichub.xml";

	@XmlElement(name = "album")
	private final List<Album> albums;
	@XmlElement(name = "playlist")
	private final List<PlayList> playlists;
	@XmlElement(name = "elements")
	private final List<AudioElement> elements;

	///// optimization structures, ignored in marshalling
	@XmlTransient
	private final Map<UUID, AudioElement> elementsById = new HashMap<>();

	@XmlTransient
	private final Map<String, AudioElement> elementsByName = new HashMap<>();

	@XmlTransient
	private final Map<String, PlayList> playlistByName = new HashMap<>();

	@XmlTransient
	private final Map<String, Album> albumsByName = new HashMap<>();

	/**
	 * Load the music hub from the file. If the XML doesn't exists or is corrupted, an empty hub is created.
	 * @return the create hub
	 */
	public static ServerMusicHub load() {
	    File file = new File(FILE_PATH);

	    if (!file.exists()) {
			LOGGER.log(Level.INFO, "No data found, create an empty MusicHub");
			return new ServerMusicHub();
		}

	    try {
			JAXBContext jaxbContext = JAXBContext.newInstance(ServerMusicHub.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

			ServerMusicHub output = (ServerMusicHub) jaxbUnmarshaller.unmarshal(new File(FILE_PATH));
			output.initializeOptimizationStructures();

			LOGGER.log(Level.INFO, "MusicHub loaded from file");
			return output;
		} catch (JAXBException e) {
	    	LOGGER.log(Level.SEVERE, "Couldn't load data: " + e + ". Create an empty MusicHub.");
	    	return new ServerMusicHub();
		}
	}

	public ServerMusicHub () {
		albums = new LinkedList<>();
		playlists = new LinkedList<>();
		elements = new LinkedList<>();
	}

	private void initializeOptimizationStructures() {
		for (Album v : albums)
			albumsByName.put(v.getTitle().toLowerCase(), v);
		for (PlayList v : playlists)
			playlistByName.put(v.getTitle().toLowerCase(), v);
		for (AudioElement v : elements) {
			elementsByName.put(v.getTitle().toLowerCase(), v);
			elementsById.put(v.getUuid(), v);
		}
	}

	@Override
	public void addElement(AudioElement element) {
		elements.add(element);
		elementsByName.put(element.getTitle().toLowerCase(), element);
		elementsById.put(element.getUuid(), element);
	}

	@Override
	public void addAlbum(Album album) {
		albums.add(album);
		albumsByName.put(album.getTitle().toLowerCase(), album);
	}

	@Override
	public void addPlaylist(PlayList playlist) {
		playlists.add(playlist);
		playlistByName.put(playlist.getTitle().toLowerCase(), playlist);
	}

	@Override
	public void deletePlayList(String playListTitle) throws NoPlayListFoundException {
	    PlayList thePlayList = this.playlistByTitle(playListTitle);
		playlists.remove(thePlayList);
		playlistByName.remove(playListTitle.toLowerCase());
		LOGGER.log(Level.INFO, "Remove playlist " + playListTitle);
	}

	@WebMethod
	public void deleteAlbum(String albumTitle) throws NoAlbumFoundException {
		Album album = this.albumByTitle(albumTitle);
		albums.remove(album);
		albumsByName.remove(albumTitle.toLowerCase());
		LOGGER.log(Level.INFO, "Remove album" + albumTitle);
	}

	@WebMethod
	public void deleteElement(String elementTitle) throws NoElementFoundException {
		AudioElement element = this.elementByTitle(elementTitle);
		elements.remove(element);
		elementsById.remove(element.getUuid());
		elementsByName.remove(elementTitle.toLowerCase());
		LOGGER.log(Level.INFO, "Remove element" + elementTitle);
	}

	@Override
	public Album[] albums() {
		return albums.toArray(new Album[0]);
	}

	@Override
	public PlayList	[] playlists() {
		return playlists.toArray(new PlayList[0]);
	}

	@Override
	public AudioElement[] elements() {
		return elements.toArray(new AudioElement[0]);
	}

	@Override
	public Song[] songs() {
		List<Song> songs = new ArrayList<>();
		for (AudioElement ae : elements) {
			if (ae instanceof Song)
				songs.add((Song) ae);
		}

		return songs.toArray(new Song[0]);
	}

	@Override
	public AudioBook[] audioBooks() {
		List<AudioBook> ab = new ArrayList<>();
		for (AudioElement ae : elements) {
			if (ae instanceof AudioBook)
				ab.add((AudioBook) ae);
		}

		return ab.toArray(new AudioBook[0]);
	}

	@Override
	public Album[] getAlbumsSortedByDate() {
	    Album[] albums = albums();
	    Arrays.sort(albums, new SortByDate());

	    return albums;
	}

	@Override
	public AudioBook[] getAudiobooksSortedByAuthor() {
		AudioBook[] ab = audioBooks();
		Arrays.sort(ab, new SortByAuthor());
		return ab;
	}

	@Override
	public Song[] getAlbumSongs(String albumTitle) throws NoAlbumFoundException, NoElementFoundException {
		Album theAlbum = albumByTitle(albumTitle);
		Song[] songsInAlbum = new Song[theAlbum.getSongs().size()];

		List<UUID> songIDs = theAlbum.getSongs();
		for (int i = 0; i < songIDs.size(); i++) {
		    AudioElement e = elementById(songIDs.get(i));

		    if (!(e instanceof Song))
				throw new NoElementFoundException("Element (ID) " + songIDs.get(i) + " exists, but is not a song");
		    songsInAlbum[i] = (Song) e;
		}

		return songsInAlbum;
	}

	@Override
	public Song[] getAlbumSongsSortedByGenre (String albumTitle) throws NoAlbumFoundException, NoElementFoundException {
		Song[] songs = getAlbumSongs(albumTitle);
		return Arrays.stream(songs)
					 .sorted(new SortByGenre())
				     .toArray(Song[]::new);
	}

	@Override
	public Album albumByTitle(@NonNull String title) throws NoAlbumFoundException {
	    Album e = albumsByName.getOrDefault(title.toLowerCase(), null);

	    if (e != null)
	    	return e;

		LOGGER.log(Level.WARNING, "Couldn't find album " + title);
		throw new NoAlbumFoundException("Album " + title + " not found!");
	}

	@Override
	public PlayList playlistByTitle(@NonNull String title) throws NoPlayListFoundException {
		PlayList e = playlistByName.getOrDefault(title.toLowerCase(), null);

		if (e != null)
			return e;

		LOGGER.log(Level.WARNING, "Couldn't find playlist " + title);
		throw new NoPlayListFoundException("PlayList " + title + " not found!");
	}

	@Override
	public AudioElement elementByTitle(@NonNull String title) throws NoElementFoundException {
		AudioElement e = elementsByName.getOrDefault(title.toLowerCase(), null);

		if (e != null)
			return e;

		LOGGER.log(Level.WARNING, "Couldn't find element " + title);
		throw new NoElementFoundException("Element  " + title + " not found!");
	}

	public AudioElement elementById(@NonNull UUID id) throws NoElementFoundException {
		AudioElement e = elementsById.getOrDefault(id, null);

		if (e != null)
			return e;

		LOGGER.log(Level.WARNING, "Couldn't find element by id: " + id);
		throw new NoElementFoundException("Element (ID) " + id + " not found!");
	}

	@Override
	public void addElementToAlbum(String elementTitle, String albumTitle) throws NoAlbumFoundException, NoElementFoundException
	{
		Album theAlbum = albumByTitle(albumTitle);
		AudioElement theElement = elementByTitle(elementTitle);

		if (!(theElement instanceof Song))
		    throw new NoElementFoundException("Element " + elementTitle + " exists, but is not a song");

		LOGGER.log(Level.INFO, "Add song " + elementTitle + " to album " + albumTitle);
		theAlbum.addSong(theElement.getUuid());
	}

	@Override
	public void addElementToPlayList(String elementTitle, String playListTitle) throws NoPlayListFoundException, NoElementFoundException
	{
		PlayList thePlayList = playlistByTitle(playListTitle);
		AudioElement theElement = elementByTitle(elementTitle);

		LOGGER.log(Level.INFO, "Add element " + elementTitle + " to playlist " + playListTitle);
		thePlayList.addElement(theElement.getUuid());
	}

	@Override
	public AudioElement[] getPlaylistElements(String playList) throws NoPlayListFoundException, NoElementFoundException {
		PlayList thePlayList = playlistByTitle(playList);
		AudioElement[] elementsInPlaylist = new AudioElement[thePlayList.getElements().size()];

		List<UUID> elementsIDs = thePlayList.getElements();
		for (int i = 0; i < elementsIDs.size(); i++) {
		    elementsInPlaylist[i] = elementById(elementsIDs.get(i));
		}

		return elementsInPlaylist;
	}

	@Override
	public void save() {
	    try {
			JAXBContext jaxbContext = JAXBContext.newInstance(ServerMusicHub.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			jaxbMarshaller.marshal(this, new File(FILE_PATH));

			LOGGER.log(Level.INFO, "Data saved");
		} catch(JAXBException e) {
	        LOGGER.log(Level.SEVERE, "Couldn't save data: " + e);
		}
	}

	@Override
	public DataHandler downloadElement(String title) throws NoElementFoundException {
		AudioElement element = elementByTitle(title);
		File file = element.getDataLocation();

		if (!file.exists()) {
			LOGGER.log(Level.SEVERE, "Couldn't find audio element file: " + file);
		}

		FileDataSource dataSource = new FileDataSource(element.getDataLocation());

		return new DataHandler(dataSource);
	}
}