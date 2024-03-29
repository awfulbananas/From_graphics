package files;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Queue;

import fromics.Menu;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

//a collection of methods and classes for parsing different types of files
public class Files {
	
	//make the constructor private so no instances can be made
	private Files() {}
	
	//constructs a new Menu with the given filename for a menu file and map of menu action names
	//to menu actions
	public static Menu readMenu(String menuPath, Map<String, Runnable> menuActions) throws IOException {
		FileInputStream menuIn;
		try {
			menuIn  = new FileInputStream(new File(menuPath));
		} catch (FileNotFoundException e) {
			menuIn = null;
			e.printStackTrace();
		}
		
		List<MenuItem> items = readMenu(menuIn);
		MenuItem root = new MenuItem();
		root.hasSubMenu = true;
		root.subMenu = items;
		for(MenuItem i : items) {
			i.parent = root;
		}
		return new Menu(root, menuActions);
	}
	
	//returns a list of menu items read from a menu file with the given FileInputStream
	private static List<MenuItem> readMenu(FileInputStream in) throws IOException {
		List<MenuItem> menu = new ArrayList<>();
		while(true) {
			menu.add(readMenuItem(in));
			if(in.available() == 0) {
				return menu;
			}
			char next = (char)in.read();
			if(next == '}') {
				return menu;
			} else if(next != ',') {
				throw new IllegalArgumentException();
			}
		}
	}
	
	//returns a single menu item read from a menu file using the given FileInputStream
	private static MenuItem readMenuItem(FileInputStream in) throws IOException {
		MenuItem item = new MenuItem();
		item.text = "";
		while(true) {
			char next = (char)in.read();
			if(next == '{') {
				item.hasSubMenu = true;
				item.subMenu = readMenu(in);
				for(MenuItem i : item.subMenu) {
					i.parent = item;
				}
				item.identifier = "m";
				return item;
			} else if(next == '[') {
				item.hasSubMenu = false;
				String identifier = "";
				boolean readingIdentifier = true;
				while(readingIdentifier) {
					char i = (char)in.read();
					if(i == ']') {
						readingIdentifier = false;
					} else {
						identifier += i;
					}
				}
				item.identifier = identifier;
				return item;
			} else {
				item.text += next;
			}
		}
	}
	
	//a class representing an item within a menu
	//it's public so that menus can either be constructed 
	//Programmatically or loaded from a file
	public static class MenuItem {
		private boolean hasSubMenu;
		private MenuItem parent;
		private String text;
		private List<MenuItem> subMenu;
		private String identifier;
		
		public String getIdentifier() {
			return identifier;
		}
		
		public String getText() {
			return text;
		}
		
		public List<MenuItem> getSubMenu() {
			if(hasSubMenu) {
				return subMenu;
			}
			return null;
		}
		
		public MenuItem getParent() {
			return parent;
		}
	}
	
	
	
	//parses an xml file conforming to the xml 1.1 standard
	//which has both xml type and doctype declarations,
	//into the XMLDoc type and returns it
	
	//doesn't read the doctype or xml type
	//TODO: make it read doctype and xml version info
	//throws an IllegalArgumentException if the xml file is incorrectly formatted
	public static XMLDoc parseXMLFile(File xmlFile) throws FileNotFoundException {
		XMLDoc document = new XMLDoc();
		FileInputStream docReader = new FileInputStream(xmlFile);
		System.out.println("reading file");
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
			
			System.out.println("read xml type");
			
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
		System.out.println("name: " + name);
		System.out.println(name.endsWith(">"));
		//if the tag is closed, don't read attributes
		if(name.endsWith(">")) {
			System.out.println("term: " + terminator);
			if(name.equals(terminator)) return null;
			element.name = name.substring(0, name.length() - 1);
		} else {
			element.name = name;
			
			//read element attributes
			boolean readingAttributes = true;
			element.attributeValues = new HashMap<>();
			element.attributes = new ArrayList<>();
			while(readingAttributes) {
				
				char firstAttributeChar = ' ';
				while(Character.isWhitespace(firstAttributeChar)) firstAttributeChar = (char)xmlIn.read();
				String nextAtbName = firstAttributeChar + readNext(xmlIn, '=');
				System.out.println("atb: " + nextAtbName);
				if(nextAtbName.charAt(0) == '>') {
					readingAttributes = false;
				} else if(nextAtbName.charAt(0) == '/') {
					xmlIn.read();
					System.out.println("returned");
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
					System.out.println("returned");
					System.out.println(readAllNext(xmlIn, '<'));
					return element;
					
				}
			}
			
			//read element contents
			element.subElements = new ArrayList<>();
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
						element.subElements.add(newElement);
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
					element.subElements.add(newElement);
					firstChar = '<';
				}
			}
		}
		
		
		return element;
	}
	
	//skips through all whitespace characters present from the given FileInputStream
	//and return the first non-whitespace character read
	private static char skipWhitespace(FileInputStream in) throws IOException {
		char c = (char)in.read();
		while(Character.isWhitespace(c)) c = (char)in.read();
		return c;
	}
	
	//returns a String of the next bytes in the file from the given FileInputStream up until
	//a space character of the end of the file
	private static String readNext(FileInputStream in) throws IOException {
		return readNext(in, ' ');
	}
	
	//returns a String of the next bytes in the file from the given FileInputStream up until
	//the given end character, a whitespace character, or the end of the file
	private static String readNext(FileInputStream in, char endChar) throws IOException {
		String s = "";
		char next = (char)in.read();
		while(next != endChar && (!Character.isWhitespace(next)) && in.available() > 0) {
			System.out.println("nextchar: " + next);
			s += next;
			next = (char)in.read();
		}
		if(in.available() == 0 && !Character.isWhitespace(next)) s += next;
		return s;
	}
	
	//returns a String of the next bytes in the file from the given FileInputStream up until
	//the given end character or the end of the file
	private static String readAllNext(FileInputStream in, char endChar) throws IOException {
		String s = "";
		char next = (char)in.read();
		while(next != endChar) {
			s += next;
			next = (char)in.read();
		}
		return s;
	}
	
	
	
	//parses an svg file into an svg object and returns it
	public static SVGFile parseSVGFile(File svgFile) {
		SVGFile svg = new SVGFile();
		XMLDoc doc = null;
		try {
			doc = parseXMLFile(svgFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("failed");
		}
		XMLElement svgData = doc.root;
		
		Queue<XMLElement> pathStrings = new LinkedList<>();
		for(XMLElement element : svgData.subElements) {
			if(element.name.equals("path")) pathStrings.add(element);
		}
		
		SplinePath[] paths = new SplinePath[pathStrings.size()];
		for(int i = 0; i < paths.length; i++) {
			XMLElement nextPathElement = pathStrings.remove();
			Color pathColor;
			if(nextPathElement.attributes.contains("stroke")) {
				pathColor = Color.decode(nextPathElement.getAttributeValue("stroke"));
			} else pathColor = Color.WHITE;
			SplinePath nextPath = new SplinePath(nextPathElement.getAttributeValue("d"), pathColor);
			paths[i] = nextPath;
		}
		svg.paths = paths;
		
		return svg;
	}
}
