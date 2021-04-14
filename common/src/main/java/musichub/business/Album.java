package musichub.business;

import lombok.Getter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class Album {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	public static class DateAdapter extends XmlAdapter<String, Date> {
		@Override
		public String marshal(Date v) {
			return DATE_FORMAT.format(v);
		}

		@Override
		public Date unmarshal(String v) throws ParseException {
			return DATE_FORMAT.parse(v);
		}
	}

	@Getter private final String title;
	@Getter private final String artist;
	@Getter private final int lengthInSeconds;
	@Getter private final UUID uuid;

	@XmlJavaTypeAdapter(DateAdapter.class)
	@Getter private final Date date;

	@XmlElement(name = "songs")
	@Getter private final List<UUID> songs = new ArrayList<>();

	public Album (String title, String artist, int lengthInSeconds, Date date) {
		this.title = title;
		this.artist = artist;
		this.lengthInSeconds = lengthInSeconds;
		this.uuid = UUID.randomUUID();
		this.date = date;
	}

	public Album() {
		title = "";
		artist = "";
		uuid = UUID.randomUUID();
		date = new Date();
		lengthInSeconds = 0;
	}

	public void addSong (UUID song)
	{
		songs.add(song);
	}
	
	public List<UUID> getSongsRandomly() {
		List<UUID> shuffledSongs = new ArrayList<>(this.songs);
		Collections.shuffle(shuffledSongs);
		return shuffledSongs;
	}
}