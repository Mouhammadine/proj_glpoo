package musichub.business;

import lombok.Getter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.util.UUID;

@XmlAccessorType(XmlAccessType.FIELD)
public class AudioBook extends AudioElement {
	@Getter private final Language language;
	@Getter private final Category category;

	public AudioBook () {
		super("", "", 0, UUID.randomUUID().toString(), "");
		this.language = Language.ENGLISH;
		this.category = Category.NOVEL;
	}

	public AudioBook (String title, String artist, int lengthInSeconds, String content, Language language, Category category) {
		super(title, artist, lengthInSeconds, content);

		this.language = language;
		this.category = category;
	}

	@Override
	public String toString() {
		return super.toString() + ", Language = " + getLanguage() + ", Category = " + getCategory() + "\n";
	}
}