package musichub.business;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.util.UUID;

@XmlAccessorType(XmlAccessType.FIELD)
public class Song extends AudioElement {
	@Getter @Setter private final Genre genre;

	public Song() {
		super("", "", 0, UUID.randomUUID().toString(), "");
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