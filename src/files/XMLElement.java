package files;

import java.util.List;
import java.util.Map;

//represents one element of an xml document
public class XMLElement {
	protected boolean isTag;
	protected String name;
	protected List<XMLElement> subElements;
	protected List<String> attributes;
	protected Map<String, String> attributeValues;
	
	protected XMLElement() {}
	
	public boolean isTag() {
		return isTag;
	}
	
	public String name() {
		return name;
	}
	
	public int elementCount() {
		return subElements.size();
	}
	
	public XMLElement getElement(int n) {
		return subElements.get(n);
	}
	
	public int attributeCount() {
		return attributes.size();
	}
	
	public String getAttribute(int n) {
		return attributes.get(n);
	}
	
	public String getAttributeValue(String atb) {
		return attributeValues.get(atb);
	}
	
	public String toString() {
		String s = name;
		if(isTag) {
			s += ":";
			if(attributes != null) {
				s += "\n	attributes:" ;
				for(String atb : attributes) {
					s += "[" + atb + ": " + attributeValues.get(atb) + "] ";
				}
			}
			if(subElements != null) {
				s += "\n	elements: [\n";
				for(XMLElement elm : subElements) {
					s += elm + ",\n";
				}
				s += "]";
			}
		}
		return s;
	}
}