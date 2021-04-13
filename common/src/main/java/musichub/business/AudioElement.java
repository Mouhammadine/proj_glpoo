package musichub.business;

import lombok.Getter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import java.io.File;
import java.util.UUID;

/**
 * Class representing a generic audio element<br>
 *
 * Known implementations:<br>
 * - {@link musichub.business.Song}<br>
 * - {@link musichub.business.AudioBook}<br>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({
	AudioBook.class, Song.class
})
public abstract class AudioElement {
	/**
	 * Title of the element
	 */
	@Getter protected final String  title;
	/**
	 * Artist who created the element
	 */
	@Getter protected final String 	artist;
	/**
	 * Duration of the element in seconds
	 */
	@Getter protected final int    	lengthInSeconds;
	/**
	 * Unique id used to identify this element
	 */
	@Getter protected final UUID    uuid;
	/**
	 * Content (path) of the element
	 */
	@Getter protected final String	content;

	public AudioElement (String title, String artist, int lengthInSeconds, String content) {
		this.title = title;
		this.artist = artist;
		this.lengthInSeconds = lengthInSeconds;
		this.content = content;
		this.uuid =  UUID.randomUUID();
	}

	/**
	 * @return File location of the audio element
	 */
	public File getDataLocation() {
		return new File(this.content);
	}

	@Override
	public String toString() {
		return "Title = " + this.title + ", Artist = " + this.artist + ", Length = " + this.lengthInSeconds + ", Content = " + this.content;
	}
}