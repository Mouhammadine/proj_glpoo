package musichub.business;

import lombok.Getter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Class representing a play list<br>
 *
 * A play list is a set of {@link musichub.business.AudioElement}
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class PlayList {
	/**
	 * Title of the play list
	 */
	@Getter private final String title;
	/**
	 * Unique id used to identify the play list
	 */
	@Getter private final UUID uuid;

	/**
	 * List of play list's elements
	 */
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

	/**
	 * Add an element to the play list
	 * @param element the element
	 */
	public void addElement (UUID element)
	{
		elements.add(element);
	}
}