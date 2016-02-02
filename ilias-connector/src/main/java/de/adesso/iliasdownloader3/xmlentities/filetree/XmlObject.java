package de.adesso.iliasdownloader3.xmlentities.filetree;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.val;
import org.simpleframework.xml.*;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static de.adesso.iliasdownloader2.util.Functions.cleanFileName;
import static de.adesso.iliasdownloader2.util.Functions.iliasXmlStringToDate;


//@XmlAccessorType(XmlAccessType.FIELD)
@Root(name = "Object", strict = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class XmlObject {

	@Attribute(name = "type", required = false)
//	@XmlAttribute(name="type")
	private String type;

	@Attribute(name = "obj_id", required = false)
//	@XmlAttribute(name="obj_id")
	private long objId;

	@Element(name = "Title", required = false)
//	@XmlElement(name="Title")
	private String title;

	@Element(name = "Description", required = false)
//	@XmlElement(name="Description")
	private String description;

	@Element(name = "Owner", required = false)
//	@XmlElement(name="Owner")
	private long owner;

	@Element(name = "CreateDate", required = false)
//	@XmlElement(name="CreateDate")
	private String createdDate;

	@Element(name = "LastUpdate", required = false)
//	@XmlElement(name="LastUpdate")
	private String updatedDate;

	@Element(name = "ImportId", required = false)
//	@XmlElement(name="ImportId")
	private String importId;

	//	@XmlElementWrapper(name="Properties")
//	@XmlElement(name="Property")
	@ElementList(name = "Properties", required = false)
	private List<XmlProperty> properties;

	//	@XmlElement(name="References")
	@ElementList(name = "References", inline = true, required = false)
	private List<XmlReference> references;

	//	@XmlTransient
	@Transient
	private List<XmlObject> children;

	private static final String FILE = "file",
			FOLDER = "fold",
			EXC = "exc",
			COURSE = "crs",
			FORUM = "frm",
			WEBLINK = "webr",
			ETHERPAD = "xpdl",
			EXERCISE = "exc",
			DATABASE = "dcl",
			OBJECTBLOCK = "itgr",
			WIKI = "wiki",
			BLOG = "blog";

	private static final String FILE_SIZE = "fileSize", FILE_EXTENSION = "fileExtension", FILE_VERSION = "fileVersion";

	public Date getCreatedDate() {
		return iliasXmlStringToDate(createdDate);
	}

	public Date getUpdatedDate() {
		return iliasXmlStringToDate(updatedDate);
	}

	public long getRefIdOne() {
		return !references.isEmpty() ? references.get(0).getRefId() : -1;
	}

	public List<Long> getRefIds() {
		return references.stream().map(XmlReference::getRefId).collect(Collectors.toCollection(LinkedList::new));
	}

	public boolean isFolder() {
		return FOLDER.equals(getType());
	}

	public boolean isFile() {
		return FILE.equals(getType());
	}

	public boolean isCourse() {
		return COURSE.equals(getType());
	}

	public boolean isBlog() {
		return BLOG.equals(getType());
	}

	public boolean isWiki() {
		return WIKI.equals(getType());
	}

	public boolean isObjectBlock() {
		return OBJECTBLOCK.equals(getType());
	}

	public boolean isDatabase() {
		return DATABASE.equals(getType());
	}

	public boolean isExercise() {
		return EXERCISE.equals(getType());
	}

	public boolean isEtherpad() {
		return ETHERPAD.equals(getType());
	}

	public boolean isWeblink() {
		return WEBLINK.equals(getType());
	}

	/**
	 * Returns the Path without the Filename
	 *
	 * @return
	 */
	public String getPath() {
		return getPath(false);
	}

	public String getPathComplete() {
		return getPath(true);
	}

	private String getPath(boolean fullpath) {
		val pathList = references.get(0).getPathEntries();
		String s = "";
		if (fullpath || !isCourse()) {
			for (int i = pathList.size() - 1; i >= 0; i--) {
				XmlPathElement pathElement = pathList.get(i);
				s = cleanFileName(pathElement.getName()) + "/" + s;

				if (!fullpath && (pathElement.getType().equals(COURSE))) {
					break;
				}
			}
		}

		s = s + (isFolderType() ? "/" + cleanFileName(getTitle()) : "");
		return s.startsWith("/") ? s.substring(1) : s;
	}

	public boolean isFolderType() {
		return isFolder() || isExercise() || isCourse();
	}

	public String getFileNameClean() {
		return cleanFileName(getTitle());
	}

	public String getCourseName() {
		if (isCourse()) {
			return getTitle();
		}

		for (XmlPathElement p : getReferences().get(0).getPathEntries()) {
			if (p.getType().equals(COURSE)) {
				return p.getName();
			}
		}

		return "";
	}

	private String getPropertyValue(String name) {
		for (val p : properties) {
			if (p.getKey().equals(name)) {
				return p.getValue();
			}
		}

		throw new RuntimeException("No Property with name: " + name + " found in " + XmlObject.class.getName() + " " + this);
	}

	/**
	 * FileSize in bytes
	 *
	 * @return
	 */
	public long getFileSize() {
		return Long.parseLong(getPropertyValue(FILE_SIZE));
	}

	public String getFileExtension() {
		return getPropertyValue(FILE_EXTENSION);
	}

	public int getFileVersion() {
		return Integer.parseInt(getPropertyValue(FILE_VERSION));
	}

}
