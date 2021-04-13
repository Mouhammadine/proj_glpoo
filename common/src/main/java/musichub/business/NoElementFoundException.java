package musichub.business;

import java.lang.Exception;

/**
 * Exception threw when an {@link musichub.business.AudioElement} isn't found
 */
public class NoElementFoundException extends Exception {
	public NoElementFoundException (String msg) {
		super(msg);
	}
}