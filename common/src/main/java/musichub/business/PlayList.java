package musichub.business;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.util.ArrayList;
import java.util.UUID;

@XmlAccessorType(XmlAccessType.FIELD)
public class PlayList {
	private String title;
	private UUID uuid;
	private ArrayList<UUID> elementUUIDs;

	public PlayList (String title, String id, ArrayList<UUID> elementUUIDs) {
		this.title = title;
		this.uuid = UUID.fromString(id);
		this.elementUUIDs = elementUUIDs;
	}
	
	public PlayList (String title) {
		this.title = title;
		this.uuid = UUID.randomUUID();
		this.elementUUIDs = new ArrayList<UUID>();
	}

	public PlayList () {
	    this("");
	}

	public void addElement (UUID element)
	{
		elementUUIDs.add(element);
	}
	
	public ArrayList<UUID> getElements() {
		return elementUUIDs;
	}
	
	public String getTitle() {
		return title;
	}
}