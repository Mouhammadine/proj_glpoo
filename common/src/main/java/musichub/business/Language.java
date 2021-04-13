package musichub.business;

import lombok.AllArgsConstructor;

/**
 * Language of a {@link AudioBook}
 */
@AllArgsConstructor
public enum Language {
	FRENCH,
	ENGLISH,
	ITALIAN,
	SPANISH,
	GERMAN;

	@Override
	public String toString() {
		return super.toString().toLowerCase();
	}
}