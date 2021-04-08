package musichub.business;

import java.lang.Exception;

@SuppressWarnings("serial")
public class NoElementFoundException extends Exception {
	public NoElementFoundException (String msg) {
		super(msg);
	}
}