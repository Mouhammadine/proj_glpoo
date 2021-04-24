package musichub.business;

import lombok.Getter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Class representing an audio book in MusicHub
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class AudioBook extends AudioElement {
	/**
	 * Language of the AudioBook
	 */
	@Getter private final Language language;
	/**
	 * Category of the AudioBook
	 */
	@Getter private final Category category;

	private AudioBook() {
		super("", "", 0, "");
		this.language = Language.ENGLISH;
		this.category = Category.NOVEL;
	}

	public AudioBook(String title, String artist, int lengthInSeconds, String content, Language language, Category category) {
		super(title, artist, lengthInSeconds, content);

		this.language = language;
		this.category = category;
	}

	@Override
	public String toString() {
		return super.toString() + ", Language = " + getLanguage() + ", Category = " + getCategory() + "\n";
	}
}