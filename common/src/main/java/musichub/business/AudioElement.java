package musichub.business;

import lombok.Getter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import java.io.File;
import java.util.UUID;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({
	AudioBook.class, Song.class
})
public abstract class AudioElement {
	@Getter protected final String  title;
	@Getter protected final String 	artist;
	@Getter protected final int    	lengthInSeconds;
	@Getter protected final UUID    uuid;
	@Getter protected final String	content;

	public AudioElement (String title, String artist, int lengthInSeconds, String id, String content) {
		this.title = title;
		this.artist = artist;
		this.lengthInSeconds = lengthInSeconds;
		this.uuid = UUID.fromString(id);
		this.content = content;
	}

	public AudioElement (String title, String artist, int lengthInSeconds, String content) {
		this.title = title;
		this.artist = artist;
		this.lengthInSeconds = lengthInSeconds;
		this.content = content;
		this.uuid =  UUID.randomUUID();
	}

	public File getDataLocation() {
		return new File(this.content);
	}

	@Override
	public String toString() {
		return "Title = " + this.title + ", Artist = " + this.artist + ", Length = " + this.lengthInSeconds + ", Content = " + this.content;
	}
}