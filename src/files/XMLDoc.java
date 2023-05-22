package files;

//represents an entire xml document
//TODO: add xml version and doctype data
public class XMLDoc {
	public XMLElement root;
	
	protected XMLDoc() {}
	
	public String toString() {
		return root.toString();
	}
}
