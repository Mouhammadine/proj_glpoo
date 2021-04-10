package musichub.server;

import musichub.business.*;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.jws.WebService;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.util.*;

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

@XmlRootElement(name = "musichub")
@WebService(endpointInterface = "musichub.business.IMusicHub")
public class ServerMusicHub implements IMusicHub {
	public static final String DIR = System.getProperty("user.dir");
	public static final String FILE_PATH = DIR + "musichub.xml";

	@XmlElement(name = "album")
	private List<Album> albums;
	@XmlElement(name = "playlist")
	private List<PlayList> playlists;
	@XmlElement(name = "elements")
	private List<AudioElement> elements;

	public static ServerMusicHub load() {
	    File file = new File(FILE_PATH);

	    if (!file.exists()) {
	    	System.err.println("No data found, create an empty MusicHub");
	    	return new ServerMusicHub();
		}

	    try {
			JAXBContext jaxbContext = JAXBContext.newInstance(ServerMusicHub.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

			return (ServerMusicHub) jaxbUnmarshaller.unmarshal(new File(FILE_PATH));
		} catch (JAXBException e) {
	    	System.err.println("Couldn't load data: " + e);
	    	System.err.println("Create an empty MusicHub");
	    	return new ServerMusicHub();
		}
	}

	public ServerMusicHub () {
		albums = new LinkedList<>();
		playlists = new LinkedList<>();
		elements = new LinkedList<>();
	}

	@Override
	public void addElement(AudioElement element) {
		elements.add(element);
	}

	@Override
	public void addAlbum(Album album) {
		albums.add(album);
	}

	@Override
	public void addPlaylist(PlayList playlist) {
		playlists.add(playlist);
	}

	@Override
	public void deletePlayList(String playListTitle) throws NoPlayListFoundException {

		PlayList thePlayList = null;
		boolean result = false;
		for (PlayList pl : playlists) {
			if (pl.getTitle().equalsIgnoreCase(playListTitle)) {
				thePlayList = pl;
				break;
			}
		}

		if (thePlayList != null)
			result = playlists.remove(thePlayList); 
		if (!result) throw new NoPlayListFoundException("Playlist " + playListTitle + " not found!");
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
	public String getAlbumsTitlesSortedByDate() {
		StringBuilder titleList = new StringBuilder();
		albums.sort(new SortByDate());
		for (Album al : albums)
			titleList.append(al.getTitle()).append("\n");
		return titleList.toString();
	}

	@Override
	public String getAudiobooksTitlesSortedByAuthor() {
		StringBuilder titleList = new StringBuilder();
		List<AudioElement> audioBookList = new ArrayList<>();
		for (AudioElement ae : elements)
			if (ae instanceof AudioBook)
				audioBookList.add(ae);
		audioBookList.sort(new SortByAuthor());
		for (AudioElement ab : audioBookList)
			titleList.append(ab.getArtist()).append("\n");
		return titleList.toString();
	}

	@Override
	public Song[] getAlbumSongs(String albumTitle) throws NoAlbumFoundException {
		Album theAlbum = albumByTitle(albumTitle);
		Song[] songsInAlbum = new Song[theAlbum.getSongs().size()];

		List<UUID> songIDs = theAlbum.getSongs();
		for (int i = 0; i < songIDs.size(); i++) {
			UUID id = songIDs.get(i);
			for (AudioElement el : elements) {
				if ((el instanceof Song) && el.getUUID().equals(id))
					songsInAlbum[i] = (Song) el;
			}
		}

		return songsInAlbum;
	}

	@Override
	public Song[] getAlbumSongsSortedByGenre (String albumTitle) throws NoAlbumFoundException {
		Song[] songs = getAlbumSongs(albumTitle);
		return Arrays.stream(songs)
					 .sorted(new SortByGenre())
				     .toArray(Song[]::new);
	}

	@Override
	public Album albumByTitle(String title) throws NoAlbumFoundException {
		for (Album e : albums) {
			if (e.getTitle().equalsIgnoreCase(title))
				return e;
		}

		throw new NoAlbumFoundException("Album " + title + " not found!");
	}

	@Override
	public PlayList playlistByTitle(String title) throws NoPlayListFoundException {
		for (PlayList e : playlists) {
			if (e.getTitle().equalsIgnoreCase(title))
				return e;
		}
		throw new NoPlayListFoundException("PlayList " + title + " not found!");
	}

	@Override
	public AudioElement elementByTitle(String title) throws NoElementFoundException {
		for (AudioElement ae : elements) {
			if (ae.getTitle().equalsIgnoreCase(title))
				return ae;
		}
		throw new NoElementFoundException("PlayList " + title + " not found!");
	}

	@Override
	public void addElementToAlbum(String elementTitle, String albumTitle) throws NoAlbumFoundException, NoElementFoundException
	{
		Album theAlbum = albumByTitle(albumTitle);
		AudioElement theElement = elementByTitle(elementTitle);

		if (!(theElement instanceof Song))
		    throw new NoElementFoundException("Element " + elementTitle + " exists, but is not a song");

		theAlbum.addSong(theElement.getUUID());
	}

	@Override
	public void addElementToPlayList(String elementTitle, String playListTitle) throws NoPlayListFoundException, NoElementFoundException
	{
		PlayList thePlayList = playlistByTitle(playListTitle);
		AudioElement theElement = elementByTitle(elementTitle);

		thePlayList.addElement(theElement.getUUID());
	}

	@Override
	public void save () {
	    try {
			JAXBContext jaxbContext = JAXBContext.newInstance(ServerMusicHub.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			jaxbMarshaller.marshal(this, new File(FILE_PATH));
		} catch(JAXBException e) {
			System.err.println("Couldn't save data: " + e);
		}
	}

	@Override
	public DataHandler downloadElement(String title) throws NoElementFoundException {
		AudioElement element = elementByTitle(title);
		FileDataSource dataSource = new FileDataSource(element.getDataLocation());

		return new DataHandler(dataSource);
	}
}