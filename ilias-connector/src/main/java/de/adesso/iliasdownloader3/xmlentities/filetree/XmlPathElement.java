package de.adesso.iliasdownloader3.xmlentities.filetree;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

//@XmlAccessorType(XmlAccessType.FIELD)
@Root(name = "Element", strict = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class XmlPathElement {

	@Attribute(name = "ref_id", required = false)
//	@XmlAttribute(name="ref_id")
	private long refId;

	@Attribute(name = "type", required = false)
//	@XmlAttribute(name="type")
	private String type;

	//	@XmlValue
	@Text(required = false)
	private String name;
}
