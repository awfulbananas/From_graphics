package fromics;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

//a collection of methods and classes for parsing different types of files
public class Files {
	
	//represents one element of an xml document
	public static class XMLElement {
		public boolean isTag;
		public String name;
		public List<XMLElement> subComponents;
		public List<String> attributes;
		public Map<String, String> attributeValues;
		
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
				if(subComponents != null) {
					s += "\n	elements: [\n";
					for(XMLElement elm : subComponents) {
						s += elm + ",\n";
					}
					s += "]";
				}
			}
			return s;
		}
	}
	
	//represents an entire xml document
	//TODO: add xml version and doctype data
	public static class XMLDoc {
		public XMLElement root;
		
		public String toString() {
			return root.toString();
		}
	}
	
	//parses an xml file conforming to the xml 1.1 standard
	//which has both xml type and doctype declarations,
	//into the XMLDoc type and returns it
	
	//doesn't read the doctype or xml type
	//TODO: make it read doctype and xml version info
	//throws an IllegalArgumentException if the xml file is incorrectly formatted
	public static XMLDoc parseXMLFile(File xmlFile) throws FileNotFoundException {
		String fileName = xmlFile.getName();
		XMLDoc document = new XMLDoc();
		FileInputStream docReader = new FileInputStream(xmlFile);
		try {
			//read past xml type
			int bracketNum = 0;
			boolean xmlTypeRead = false;
			while((!xmlTypeRead) || bracketNum != 0) {
				char next = (char)docReader.read();
				if(next == '<') {
					bracketNum++;
					
					String tagName = readNext(docReader);
					if(tagName.equals("?xml")) {
						xmlTypeRead = true;
					}
				}
				
				if(next == '>') bracketNum--;
			}
			
			//read past doctype
			boolean doctypeRead = false;
			while((!doctypeRead) || bracketNum != 0) {
				char next = (char)docReader.read();
				if(next == '<') {
					bracketNum++;
					
					String tagName = readNext(docReader);
					if(tagName.equals("!DOCTYPE")) {
						doctypeRead = true;
					}
				}
				
				if(next == '>') bracketNum--;
			}
			
			//read up to the first element
			while((char)docReader.read() != '<');
			
			document.root = parseXML(docReader, "");
		} catch(IOException e) {
			System.out.println("IOException: \n" + e.getMessage());
		}
		return document;
	}
	
	//parses one xml element, assuming the FileInputStream has just read the left bracket
	//of the element
	private static XMLElement parseXML(FileInputStream xmlIn, String terminator) throws IOException {
		XMLElement element = new XMLElement();
		element.isTag = true;
		String name = readNext(xmlIn);
		//if the tag is closed, don't read attributes
		if(name.endsWith(">")) {
			if(name.equals(terminator)) return null;
			element.name = name.substring(0, name.length() - 1);
		} else {
			element.name = name;
			
			//read element attributes
			boolean readingAttributes = true;
			element.attributeValues = new HashMap<>();
			element.attributes = new ArrayList<>();
			while(readingAttributes) {
				
				System.out.println("reading attributes of " + element.name);
				
				char firstAttributeChar = ' ';
				while(Character.isWhitespace(firstAttributeChar)) firstAttributeChar = (char)xmlIn.read();
				String nextAtbName = firstAttributeChar + readNext(xmlIn, '=');
				if(nextAtbName.charAt(0) == '>') {
					readingAttributes = false;
				} else if(nextAtbName.charAt(0) == '/') {
					xmlIn.read();
					return element;
				} else {
					element.attributes.add(nextAtbName);
					//throw an error if the data doesn't immediately follow the attribute name,
					//or isn't enclosed in quotes
					char nextChar = (char)xmlIn.read();
					if(nextChar != '"') {
						throw new IllegalArgumentException();
					}
					
					String data = readAllNext(xmlIn, '"');
					element.attributeValues.put(nextAtbName, data);
				}
				
				//if the tag is closed, stop reading attributes,
				//and if the element is terminated, return the element
				char nextChar = (char)xmlIn.read();
				if(nextChar == '>') {
					readingAttributes = false;
				} else if(nextChar =='/') {
					readAllNext(xmlIn, '<');
					return element;
				}
			}
			
			//read element contents
			element.subComponents = new ArrayList<>();
			boolean readingContents = true;
			char firstChar = skipWhitespace(xmlIn);
			while(readingContents) {
				if(firstChar == '<') {
					XMLElement newElement = parseXML(xmlIn, "/" + element.name + ">");
					//if the closing tag is found, return the element
					if(newElement == null) {
						//I don't actually need this boolean, but while(true) looks bad
						readingContents = false;
						return element;
					} else {
						element.subComponents.add(newElement);
					}
				} else {
					//if the contents aren't a tag, make it a text element
					XMLElement newElement = new XMLElement();
					newElement.isTag = false;
					newElement.name = firstChar + readAllNext(xmlIn, '<');
					//throw an error on bad formatting
					if(newElement.name.equals("")) {
						throw new IllegalArgumentException();
					}
					element.subComponents.add(newElement);
					firstChar = '<';
				}
			}
		}
		
		
		return element;
	}
	
	private static char skipWhitespace(FileInputStream in) throws IOException {
		char c = (char)in.read();
		while(Character.isWhitespace(c)) c = (char)in.read();
		return c;
	}
	
	private static String readNext(FileInputStream in) throws IOException {
		return readNext(in, ' ');
	}
	
	private static String readNext(FileInputStream in, char endChar) throws IOException {
		String s = "";
		char next = (char)in.read();
		while(next != endChar && !Character.isWhitespace(next) && in.available() > 0) {
			s += next;
			next = (char)in.read();
		}
		return s;
	}
	
	private static String readAllNext(FileInputStream in, char endChar) throws IOException {
		String s = "";
		char next = (char)in.read();
		while(next != endChar) {
			s += next;
			next = (char)in.read();
		}
		return s;
	}
}
