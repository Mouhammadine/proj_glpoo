package musichub.business;

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