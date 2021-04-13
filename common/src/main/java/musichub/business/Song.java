package musichub.business;

import lombok.Getter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Class representing an audio book in MusicHub
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Song extends AudioElement {
	/**
	 * Genre of the song
	 */
	@Getter private final Genre genre;

	public Song() {
		super("", "", 0, "");
		this.genre = Genre.CLASSIC;
	}

	public Song (String title, String artist, int length, String content, Genre genre) {
		super (title, artist, length, content);
		this.genre = genre;
	}

	@Override
	public String toString() {
		return super.toString() + ", Genre = " + getGenre() + "\n";
	}
}