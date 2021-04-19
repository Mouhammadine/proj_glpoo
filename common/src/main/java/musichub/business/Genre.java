package musichub.business;

/**
 * Genre of a {@link Song}
 */
public enum Genre {
	JAZZ,
	CLASSIC,
	HIPHOP,
	ROCK,
	POP,
	RAP,
	METAL;

	@Override
	public String toString() {
		return super.toString().toLowerCase();
	}
}