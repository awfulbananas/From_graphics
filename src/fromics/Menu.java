package fromics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.List;
import java.util.Map;

import files.Files.MenuItem;

//a class representing a keyboard controlled nested menu
//the MenuItem class is located in the Files class rather than
//here because it needs to be readily accessible to Files
//to easily load menus from file
public class Menu extends Linkable {
	//TODO: make these constants changable for more flexibility, making these the defaults rather than the only option
	
	//the default font size for the text of the buttons of the menu
	public static final int MENU_FONT_SIZE = 20;
	//the default font for the text of the menu buttons
	public static final Font MENU_FONT = new Font("Arial", Font.BOLD, MENU_FONT_SIZE);
	//the default width of the menu buttons
	public static final int MENU_ITEMS_X = 450;
	//the default height ofthe menu buttons
	public static final int MENU_ITEMS_Y = 200;
	//the default vertical separation between menu buttons
	public static final int MENU_ITEMS_SEPERATION = 50;
	//the default size of the highlight showing which menu button is currently
	public static final int MENU_HIGHLIGHT_SIZE = 5;
	
	//the root menu item for the menu, which only acts as a connector and is never displayed or used
	private MenuItem root;
	//a List of the buttons of the menu which are currently visible and accessible
	protected List<MenuItem> current;
	//the index in the list of currently visible menu items of the currently selected button
	protected int selected;
	//a Map of actions to be run for buttons with the given action tag in the menu file
	private Map<String, Runnable> actions;
	
	//constructs a new Menu with the given MenuItem as the root of the menu,
	//and the given Map of menu action tag Strings to actions
	public Menu(MenuItem r, Map<String, Runnable> actions) {
		super(0, 0);
		this.root = r;
		this.actions = actions;
		current = root.getSubMenu();
		actions.put("b", () -> back());
		actions.put("m", () -> current = current.get(selected).getSubMenu());
	}
	
	//the method run to go back up a menu when the relevant button is pressed
	public void back() {
		selected = 0;
		if(current.get(0).getParent() != root) current = current.get(0).getParent().getParent().getSubMenu();
	}
	
	//changes the selected menu button to the one above the currently selected one, looping from top to bottom
	public void moveUp() {
		selected += current.size() - 1;
		selected %= current.size();
	}
	
	//changes the selected menu button to the one below the currently selected one, looping from top to bottom
	public void moveDown() {
		selected++;
		selected %= current.size();
	}
	
	//runs the action associated with the currently selected menu item
	public void select() {
		String identifier = current.get(selected).getIdentifier();
		if(actions.containsKey(identifier)) {
			actions.get(identifier).run();
		}
	}
	
	//draws this Menu to the screen with the given Graphics object and offsets
	@Override
	protected void draw(Graphics g, double xOff, double yOff, double angOff) {
		g.setFont(MENU_FONT);
		for(int i = 0; i < current.size(); i++) {
			setColor(Color.black);
			setDefColor(g);
			int y = MENU_ITEMS_Y + MENU_ITEMS_SEPERATION * i;
			if(i == selected) {
				g.fillRect(MENU_ITEMS_X - MENU_HIGHLIGHT_SIZE, y - MENU_HIGHLIGHT_SIZE, 100 + 2 * MENU_HIGHLIGHT_SIZE, 25 + 2 * MENU_HIGHLIGHT_SIZE);
			}
			
			String name = current.get(i).getText();
			setColor(Color.white);
			setDefColor(g);
			g.fillRect(MENU_ITEMS_X, y, 100, 25);
			setColor(Color.black);
			setDefColor(g);
			g.drawRect(MENU_ITEMS_X, y, 100, 25);
			g.drawString(name, MENU_ITEMS_X + 5, y + 20);
		}
	}
}
