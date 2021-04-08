package musichub.business;

import java.lang.Exception;

@SuppressWarnings("serial")
public class NoAlbumFoundException extends Exception {
	public NoAlbumFoundException (String msg) {
		super(msg);
	}
}