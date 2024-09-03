package fromics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import files.Files.MenuItem;

//a class representing a keyboard controlled nested menu
//the MenuItem class is located in the Files class rather than
//here because it needs to be readily accessible to Files
//to easily load menus from file
public class Menu extends Linkable {
	//TODO: make these constants changable for more flexibility, making these the defaults rather than the only option
	
	//the default font size for the text of the buttons of the menu
	public static final int DEF_MENU_FONT_SIZE = 20;
	//the default font for the text of the menu buttons
	public static final Font DEF_MENU_FONT = new Font("Arial", Font.BOLD, DEF_MENU_FONT_SIZE);
	//the default width of the menu buttons
	public static final int DEF_MENU_ITEMS_X = 450;
	//the default height ofthe menu buttons
	public static final int DEF_MENU_ITEMS_Y = 200;
	//the default vertical separation between menu buttons
	public static final int DEF_MENU_ITEMS_SEPERATION = 50;
	//the default size of the highlight showing which menu button is currently
	public static final int DEF_MENU_HIGHLIGHT_SIZE = 5;

	public static final Color DEF_ITEM_COLOR = Color.WHITE;
	public static final Color DEF_HIGHLIGHT_COLOR = Color.WHITE;
	public static final Color DEF_ITEM_BORDER_COLOR = Color.BLACK;
	public static final Color DEF_ITEM_TEXT_COLOR = Color.BLACK;
	
	//the root menu item for the menu, which only acts as a connector and is never displayed or used
	private MenuItem root;
	//a List of the buttons of the menu which are currently visible and accessible
	protected List<MenuItem> current;
	//the index in the list of currently visible menu items of the currently selected button
	protected int selected;
	//a Map of actions to be run for buttons with the given action tag in the menu file
	private Map<String, Runnable> actions;

	private Font menuFont;
	private int menuItemsX;
	private int menuItemsY;
	private int menuItemsSeperation;
	private int menuHighlightSize;
	private Color menuItemColor;
	private Color menuHighlightColor;
	private Color menuBorderColor;
	private Color menuTextColor;
	
	//constructs a new Menu with the given MenuItem as the root of the menu,
	//and the given Map of menu action tag Strings to actions
	public Menu(MenuItem r, Map<String, Runnable> actions) {
		super(0, 0);
		this.root = r;
		this.actions = actions;
		current = root.getSubMenu();
		actions.put("b", this::back);
		actions.put("m", () -> current = current.get(selected).getSubMenu());
	}

	public void loadConfigFile(File configFile) throws FileNotFoundException {
		Scanner in = new Scanner(configFile);
		String[] fontArgs = in.nextLine().split(",");
		menuFont = new Font(fontArgs[0], Integer.parseInt(fontArgs[1]), Integer.parseInt(fontArgs[2]));
		menuItemsX = in.nextInt();
		menuItemsY = in.nextInt();
		menuItemsSeperation = in.nextInt();
		menuHighlightSize = in.nextInt();
		menuItemColor = new Color(in.nextInt(),in.nextInt(),in.nextInt(),in.nextInt());
		menuHighlightColor = new Color(in.nextInt(),in.nextInt(),in.nextInt(),in.nextInt());
		menuBorderColor = new Color(in.nextInt(),in.nextInt(),in.nextInt(),in.nextInt());
		menuTextColor = new Color(in.nextInt(),in.nextInt(),in.nextInt(),in.nextInt());
	}

	public void loadDefaultConfig() {
		Font menuFont = DEF_MENU_FONT;
		int menuItemsX = DEF_MENU_ITEMS_X;
		int menuItemsY = DEF_MENU_ITEMS_Y;
		int menuItemsSeperation = DEF_MENU_ITEMS_SEPERATION;
		int menuHighlightSize = DEF_MENU_HIGHLIGHT_SIZE;
		Color menuItemColor = DEF_ITEM_COLOR;
		Color menuHighlightColor = DEF_HIGHLIGHT_COLOR;
		Color menuBorderColor = DEF_ITEM_BORDER_COLOR;
		Color menuTextColor = DEF_ITEM_TEXT_COLOR;
	}
	
	//the method run to go back up a menu when the relevant button is pressed
	public void back() {
		selected = 0;
		if(current.getFirst().getParent() != root) current = current.getFirst().getParent().getParent().getSubMenu();
	}

	public void addDefaultKeyBehaviour() {
		addKeystrokeFunction((KeyEvent e) -> {
			switch (e.getKeyCode()) {
				case KeyEvent.VK_UP:
					moveUp();
					break;
				case KeyEvent.VK_DOWN:
					moveDown();
					break;
				case KeyEvent.VK_ENTER, KeyEvent.VK_SPACE:
					select();
					break;
			}
		});
	}
	
	//changes the selected menu button to the one above the currently selected one, looping from top to bottom
	public void moveUp() {
		selected += current.size() - 1;
		selected = (selected + current.size()) % current.size();
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
	protected void draw(Graphics g, BufferedImage img, double xOff, double yOff, double angOff) {
		g.setFont(menuFont);
		int totalX = menuItemsX + (int)xOff;
		int totalY = menuItemsY + (int)yOff;
		for(int i = 0; i < current.size(); i++) {
			int y = totalY + menuItemsSeperation * i;
			if(i == selected) {
				g.setColor(menuHighlightColor);
				g.fillRect(totalX - menuHighlightSize, y - menuHighlightSize, 100 + 2 * menuHighlightSize, 25 + 2 * menuHighlightSize);
			}
			
			String name = current.get(i).getText();
			g.setColor(menuItemColor);
			g.fillRect(totalX, y, 100, 25);
			g.setColor(menuBorderColor);
			g.drawRect(totalX, y, 100, 25);
			g.setColor(menuTextColor);
			g.drawString(name, totalX + 5, y + 20);
		}
	}
}
