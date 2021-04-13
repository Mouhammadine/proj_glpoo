package musichub.business;

import java.lang.Exception;

/**
 * Exception threw when an {@link musichub.business.Album} isn't found
 */
public class NoAlbumFoundException extends Exception {
	public NoAlbumFoundException (String msg) {
		super(msg);
	}
}