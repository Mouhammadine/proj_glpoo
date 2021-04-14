package musichub.business;

import lombok.Getter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.UUID;

@XmlAccessorType(XmlAccessType.FIELD)
public class PlayList {
	@Getter private final String title;
	@Getter private final UUID uuid;

	@XmlElement(name = "elements")
	@Getter private final ArrayList<UUID> elements;

	public PlayList (String title) {
		this.title = title;
		this.uuid = UUID.randomUUID();
		this.elements = new ArrayList<UUID>();
	}

	public PlayList () {
	    this("");
	}

	public void addElement (UUID element)
	{
		elements.add(element);
	}
}