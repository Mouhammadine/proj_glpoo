package musichub.business;

import javax.activation.DataHandler;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

/**
 * Interface used to manage MusicHub data<br>
 *
 * The interface is implemented by server and used by client through JAX-WS
 */
@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface IMusicHub {
	/**
	 * Add a new audio element (song or audiobook)
	 * @param element a complete audio element object
	 */
	@WebMethod void addElement(AudioElement element);

	/**
	 * Add a new album
	 * @param album a complete album object
	 */
	@WebMethod void addAlbum(Album album);

	/**
	 * Add a new playlist
	 * @param playlist a complete playlist object
	 */
	@WebMethod void addPlaylist(PlayList playlist);

	/**
	 * Delete a playlist by his name
	 * No order is guaranteed
	 * @param playListTitle the title
	 * @throws NoPlayListFoundException if the playlist doesn't exists
	 */
	@WebMethod void deletePlayList(String playListTitle) throws NoPlayListFoundException;

	/**
	 * Get a list of all albums
	 * No order is guaranteed
	 * @return all albums
	 */
	@WebMethod Album[] albums();

	/**
	 * Get a list of all playlists
	 * No order is guaranteed
     * @return all playlists
	 */
	@WebMethod PlayList[] playlists();

	/**
	 * Get a list of all elements (audiobooks / songs)
	 * No order is guaranteed
	 * @return all elements
	 */
	@WebMethod AudioElement[] elements();

	/**
	 * Get an album by his title
	 * @param title the title
	 * @return the element
	 * @throws NoAlbumFoundException if the album doesn't exists
	 */
	@WebMethod Album albumByTitle(String title) throws NoAlbumFoundException;

	/**
	 * Get a playlist by his title
	 * @param title the title
	 * @return the playlist
	 * @throws NoPlayListFoundException if the playlist doesn't exists
	 */
	@WebMethod PlayList playlistByTitle(String title) throws NoPlayListFoundException;

	/**
	 * Get an element (audiobook / song) by his title
	 * @param title the title
	 * @return the element
	 * @throws NoElementFoundException if the element doesn't exists
	 */
	@WebMethod AudioElement elementByTitle(String title) throws NoElementFoundException;

	/**
	 * Return a formatted string containing all albums sorted by release date
	 * @return A formatted string. One line per album
	 */
	@WebMethod String getAlbumsTitlesSortedByDate();

	/**
	 * Return a formatted string containing all audiobooks sorted by author
	 * @return A formatted string. One line per audiobook
	 */
	@WebMethod String getAudiobooksTitlesSortedByAuthor();

	/**
	 * Get the album's songs. No order is guaranteed.
	 *
	 * @param albumTitle title of the album
	 * @return list of songs
	 * @throws NoAlbumFoundException if the specified album doesn't exists
	 */
	@WebMethod Song[] getAlbumSongs(String albumTitle) throws NoAlbumFoundException;

	/**
	 * Get the album's songs, sorted by genre
	 * @param albumTitle title of the album
	 * @return list of songs
	 * @throws NoAlbumFoundException if the specified album doesn't exists
	 */
	@WebMethod Song[] getAlbumSongsSortedByGenre(String albumTitle) throws NoAlbumFoundException;

	/**
	 Add a song to an album

	 @param elementTitle the element to add
	 @param albumTitle the album

	 @throws NoAlbumFoundException if the specified album doesn't exists
	 @throws NoElementFoundException if the element doesn't exists or isn't a song
	 */
	@WebMethod void addElementToAlbum(String elementTitle, String albumTitle) throws NoAlbumFoundException, NoElementFoundException;

	/**
	 Add an element (song or audio book) to a playlist

	 @param elementTitle the element to add
	 @param playListTitle the playlist

	 @throws NoPlayListFoundException if the specified playlist doesn't exists
	 @throws NoElementFoundException if the element doesn't exists
	 */
	@WebMethod void addElementToPlayList(String elementTitle, String playListTitle) throws NoPlayListFoundException, NoElementFoundException;

	/**
	 * Get playlist's elements. No order is guaranteed.
	 *
	 * @param playList title of the playlist
	 * @return list of songs
	 * @throws NoPlayListFoundException if the specified playlist doesn't exists
	 */
	@WebMethod AudioElement[] getPlaylistElements(String playList) throws NoPlayListFoundException;

	/**
	 Utility to download an element (song or audio book) from the server

	 @param title Title of the element to download
	 @return downloader
	 @throws NoElementFoundException if the element doesn't exists
	 */
	@WebMethod DataHandler downloadElement(String title) throws NoElementFoundException;

	/**
	Save data to XML on server side
	 */
	@WebMethod void save();
}