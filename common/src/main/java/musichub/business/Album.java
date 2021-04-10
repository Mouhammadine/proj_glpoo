package musichub.business;

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

	private String title;
	private String artist;
	private int lengthInSeconds;
	private UUID uuid;

	@XmlJavaTypeAdapter(DateAdapter.class)
	private Date date;

	@XmlElement(name = "songs")
	private List<UUID> songsUIDs;

	public Album(String title, String artist, int lengthInSeconds, String id, String date, ArrayList<UUID> songsUIDs) {
		this.title = title;
		this.artist = artist;
		this.lengthInSeconds = lengthInSeconds;
		this.uuid = UUID.fromString(id);
		try {
			this.date = DATE_FORMAT.parse(date);
		} catch (ParseException ex) {
			ex.printStackTrace();
		}
		this.songsUIDs = songsUIDs;
	}

	public Album (String title, String artist, int lengthInSeconds, String date) {
		this.title = title;
		this.artist = artist;
		this.lengthInSeconds = lengthInSeconds;
		this.uuid = UUID.randomUUID();
		try {
			this.date = DATE_FORMAT.parse(date);
		} catch (ParseException ex) {
			ex.printStackTrace();
		}
		this.songsUIDs = new ArrayList<>();
	}

	public Album() {
		title = "";
		artist = "";
		uuid = UUID.randomUUID();
		date = new Date();
		songsUIDs = new ArrayList<>();
	}

	public void addSong (UUID song)
	{
		songsUIDs.add(song);
	}
	
	
	public List<UUID> getSongs() {
		return songsUIDs;
	}
	
	public List<UUID> getSongsRandomly() {
		List<UUID> shuffledSongs = new ArrayList<>(this.songsUIDs);
		Collections.shuffle(shuffledSongs);
		return shuffledSongs;
	}
	
	public String getTitle() {
		return title;
	}
	
	public Date getDate() {
		return date;
	}
}