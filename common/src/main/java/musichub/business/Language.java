package musichub.business;

public enum Language {
	FRENCH ("french"),
	ENGLISH ("english"),
	ITALIAN ("italian"),
	SPANISH ("spanish"),
	GERMAN("german");

	private String language;

	Language (String language) {
		this.language = language;
	}
	public String getLanguage() {
		return language;
	}
}