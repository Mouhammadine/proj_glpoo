package musichub.business;

import java.lang.Exception;

@SuppressWarnings("serial")
public class NoPlayListFoundException extends Exception {
	public NoPlayListFoundException (String msg) {
		super(msg);
	}
}