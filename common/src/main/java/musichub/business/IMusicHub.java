package musichub.business;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import java.util.ArrayList;

@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface IMusicHub {
	@WebMethod void addElement(AudioElement element);

	@WebMethod void addAlbum(Album album);

	@WebMethod void addPlaylist(PlayList playlist);

	@WebMethod void deletePlayList(String playListTitle) throws NoPlayListFoundException;

	@WebMethod Album[] albums();

	@WebMethod PlayList[] playlists();
	@WebMethod AudioElement[] elements();

	@WebMethod String getAlbumsTitlesSortedByDate();

	@WebMethod String getAudiobooksTitlesSortedByAuthor();

	@WebMethod ArrayList<AudioElement> getAlbumSongs (String albumTitle) throws NoAlbumFoundException;

	@WebMethod ArrayList<Song> getAlbumSongsSortedByGenre (String albumTitle) throws NoAlbumFoundException;

	@WebMethod void addElementToAlbum(String elementTitle, String albumTitle) throws NoAlbumFoundException, NoElementFoundException;

	@WebMethod void addElementToPlayList(String elementTitle, String playListTitle) throws NoPlayListFoundException, NoElementFoundException;

	@WebMethod void save();
}