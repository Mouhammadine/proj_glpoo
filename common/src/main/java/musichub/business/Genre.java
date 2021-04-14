package musichub.business;

public enum Genre {
	JAZZ,
	CLASSIC,
	HIPHOP,
	ROCK,
	POP,
	RAP;

	@Override
	public String toString() {
		return super.toString().toLowerCase();
	}
}