package musichub.business;

/**
 * Category of a {@link musichub.business.AudioBook}
 */
public enum Category {
	YOUTH,
	NOVEL,
	THEATER,
	DOCUMENTARY,
	SPEECH;

	@Override
	public String toString() {
		return super.toString().toLowerCase();
	}
}