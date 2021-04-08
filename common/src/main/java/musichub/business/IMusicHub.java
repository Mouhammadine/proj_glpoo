package musichub.business;

import java.util.Iterator;
import java.util.List;

public interface IMusicHub {
	public void addElement(AudioElement element);
	
	public void addAlbum(Album album);
	
	public void addPlaylist(PlayList playlist);
	
	public void deletePlayList(String playListTitle) throws NoPlayListFoundException;
	
	public Iterator<Album> albums();
	
	public Iterator<PlayList> playlists();
	
	public Iterator<AudioElement> elements();
	
	public String getAlbumsTitlesSortedByDate();
	
	public String getAudiobooksTitlesSortedByAuthor();

	public List<AudioElement> getAlbumSongs (String albumTitle) throws NoAlbumFoundException;
	
	public List<Song> getAlbumSongsSortedByGenre (String albumTitle) throws NoAlbumFoundException;

	public void addElementToAlbum(String elementTitle, String albumTitle) throws NoAlbumFoundException, NoElementFoundException;
	
	public void addElementToPlayList(String elementTitle, String playListTitle) throws NoPlayListFoundException, NoElementFoundException;
	
	public void save();
}