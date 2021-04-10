package musichub.server;

import musichub.business.*;
import musichub.util.XMLHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.jws.WebService;
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

@WebService(endpointInterface = "musichub.business.IMusicHub")
public class ServerMusicHub implements IMusicHub {
	private final LinkedList<Album> albums;
	private final LinkedList<PlayList> playlists;
	private final LinkedList<AudioElement> elements;

	public static final String DIR = System.getProperty("user.dir");
	public static final String ALBUMS_FILE_PATH = DIR + "\\files\\albums.xml";
	public static final String PLAYLISTS_FILE_PATH = DIR + "\\files\\playlists.xml";
	public static final String ELEMENTS_FILE_PATH = DIR + "\\files\\elements.xml";

	private final XMLHandler xmlHandler = new XMLHandler();

	public ServerMusicHub () {
		albums = new LinkedList<>();
		playlists = new LinkedList<>();
		elements = new LinkedList<>();

		this.loadElements();
		this.loadAlbums();
		this.loadPlaylists();
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

	private void loadAlbums () {
		NodeList albumNodes = xmlHandler.parseXMLFile(ALBUMS_FILE_PATH);
		if (albumNodes == null) return;

		for (int i = 0; i < albumNodes.getLength(); i++) {
			if (albumNodes.item(i).getNodeType() == Node.ELEMENT_NODE)   {
				Element albumElement = (Element) albumNodes.item(i);
				if (albumElement.getNodeName().equals("album")) 	{
					try {
						this.addAlbum(new Album (albumElement));
					} catch (Exception ex) {
						System.out.println ("Something is wrong with the XML album element");
					}
				}
			}  
		}
	}

	private void loadPlaylists () {
		NodeList playlistNodes = xmlHandler.parseXMLFile(PLAYLISTS_FILE_PATH);
		if (playlistNodes == null) return;

		for (int i = 0; i < playlistNodes.getLength(); i++) {
			if (playlistNodes.item(i).getNodeType() == Node.ELEMENT_NODE)   {
				Element playlistElement = (Element) playlistNodes.item(i);
				if (playlistElement.getNodeName().equals("playlist")) 	{
					try {
						this.addPlaylist(new PlayList (playlistElement));
					} catch (Exception ex) {
						System.out.println ("Something is wrong with the XML playlist element");
					}
				}
			}  
		}
	}

	private void loadElements () {
		NodeList audioelementsNodes = xmlHandler.parseXMLFile(ELEMENTS_FILE_PATH);
		if (audioelementsNodes == null) return;

		for (int i = 0; i < audioelementsNodes.getLength(); i++) {
			if (audioelementsNodes.item(i).getNodeType() == Node.ELEMENT_NODE)   {
				Element audioElement = (Element) audioelementsNodes.item(i);
				if (audioElement.getNodeName().equals("song")) 	{
					try {
						AudioElement newSong = new Song (audioElement);
						this.addElement(newSong);
					} catch (Exception ex) 	{
						System.out.println ("Something is wrong with the XML song element");
					}
				}
				if (audioElement.getNodeName().equals("audiobook")) 	{
					try {
						AudioElement newAudioBook = new AudioBook (audioElement);
						this.addElement(newAudioBook);
					} catch (Exception ex) 	{
						System.out.println ("Something is wrong with the XML audiobook element");
					}
				}
			}  
		}
	}

	@Override
	public void save () {
		this.saveElements();
		this.saveAlbums();
		this.savePlayLists();
	}

	public void saveAlbums() {
		Document document = xmlHandler.createXMLDocument();
		if (document == null) return;

		// root element
		Element root = document.createElement("albums");
		document.appendChild(root);

		//save all albums
		for (Album currentAlbum : this.albums()) {
			currentAlbum.createXMLElement(document, root);
		}
		xmlHandler.createXMLFile(document, ALBUMS_FILE_PATH);
	}

	public void savePlayLists() {
		Document document = xmlHandler.createXMLDocument();
		if (document == null) return;

		// root element
		Element root = document.createElement("playlists");
		document.appendChild(root);

		//save all playlists
        for (PlayList currentPlayList : this.playlists()) {
			currentPlayList.createXMLElement(document, root);
		}
		xmlHandler.createXMLFile(document, PLAYLISTS_FILE_PATH);
	}

	public void saveElements() {
		Document document = xmlHandler.createXMLDocument();
		if (document == null) return;

		// root element
		Element root = document.createElement("elements");
		document.appendChild(root);

		//save all AudioElements
		for (AudioElement currentElement : elements) {
			if (currentElement instanceof Song) {
				currentElement.createXMLElement(document, root);
			}
			if (currentElement instanceof AudioBook) {
				currentElement.createXMLElement(document, root);
			}
		}
		xmlHandler.createXMLFile(document, ELEMENTS_FILE_PATH);
 	}

	@Override
	public DataHandler downloadElement(String title) throws NoElementFoundException {
		AudioElement element = elementByTitle(title);
		FileDataSource dataSource = new FileDataSource(element.getDataLocation());

		return new DataHandler(dataSource);
	}
}