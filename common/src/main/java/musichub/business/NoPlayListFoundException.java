package musichub.business;

import java.lang.Exception;

/**
 * Exception threw when a {@link musichub.business.PlayList} isn't found
 */
public class NoPlayListFoundException extends Exception {
	public NoPlayListFoundException (String msg) {
		super(msg);
	}
}