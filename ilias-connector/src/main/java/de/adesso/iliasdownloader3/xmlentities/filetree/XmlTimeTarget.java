package de.adesso.iliasdownloader3.xmlentities.filetree;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

//@XmlAccessorType(XmlAccessType.FIELD)
@Root(name = "TimeTarget")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class XmlTimeTarget {

	@Attribute(name = "type", required = false)
//	@XmlAttribute(name="type")
	private int type;

	@Element(name = "Timing", required = false)
//	@XmlElement(name="Timing")
	private XmlTiming timing;

	@Element(name = "Suggestion", required = false)
//	@XmlElement(name="Suggestion")
	private XmlTiming suggestion;
}
