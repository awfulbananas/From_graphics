package fromics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.List;
import java.util.Map;

import files.Files.MenuItem;

public class Menu extends Linkable {
	public static final int MENU_FONT_SIZE = 20;
	public static final Font MENU_FONT = new Font("Arial", Font.BOLD, MENU_FONT_SIZE);
	public static final int MENU_ITEMS_X = 450;
	public static final int MENU_ITEMS_Y = 200;
	public static final int MENU_ITEMS_SEPERATION = 50;
	public static final int MENU_HIGHLIGHT_SIZE = 5;
	public static final int MENU_ITEM_WIDTH = 200;
	
	private MenuItem root;
	protected List<MenuItem> current;
	protected int selected;
	private Map<String, Runnable> actions;
	
	public Menu(MenuItem r, Map<String, Runnable> actions) {
		super(0, 0);
		this.root = r;
		this.actions = actions;
		current = root.getSubMenu();
		actions.put("b", () -> back());
		actions.put("m", () -> current = current.get(selected).getSubMenu());
	}
	
	public void back() {
		selected = 0;
		if(current.get(0).getParent() != root) current = current.get(0).getParent().getParent().getSubMenu();
	}
	
	public void moveUp() {
		selected += current.size() - 1;
		selected %= current.size();
	}
	
	public void moveDown() {
		selected++;
		selected %= current.size();
	}
	
	public void select() {
		String identifier = current.get(selected).getIdentifier();
		if(actions.containsKey(identifier)) {
			actions.get(identifier).run();
		}
	}

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
