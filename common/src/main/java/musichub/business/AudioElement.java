package musichub.business;

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
	protected String  	title;
	protected String 	artist;
	protected int    	lengthInSeconds;
	protected UUID    	uuid;
	protected String	content;

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

	public UUID getUUID() {
		return this.uuid;
	}
	
	public String getArtist() {
		return this.artist;
	}

	public String getTitle() {
		return this.title;
	}
	
	public String toString() {
		return "Title = " + this.title + ", Artist = " + this.artist + ", Length = " + this.lengthInSeconds + ", Content = " + this.content;
	}
}