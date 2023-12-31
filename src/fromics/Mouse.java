package fromics;

import java.awt.IllegalComponentStateException;
import java.awt.MouseInfo;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Mouse extends MouseAdapter implements MouseListener {
	private Frindow win;
	Queue<MouseEvent> clickEvents;
	List<MouseClickFunction> clickFunctions;
	private boolean[] buttonStates;
	private boolean mouseInWindow;
	
	public Mouse(Frindow win) {
		this.win = win;
		buttonStates = new boolean[MouseInfo.getNumberOfButtons() + 1];
		mouseInWindow = true;
		clickEvents = new LinkedList<>();
		clickFunctions = new ArrayList<>();
	}
	
	public boolean getMouseButton(int button) {
		return buttonStates[button + 1];
	}
	
	public Point getMouseLoc() {
		try {
			return win.locOnScreenFromGlobalLoc(new Point(MouseInfo.getPointerInfo().getLocation().x, MouseInfo.getPointerInfo().getLocation().y));
		} catch(IllegalComponentStateException e) {
			return new Point();
		}
	}
	
	public boolean getMousePresent() {
		return mouseInWindow;
	}
	
	public void addMouseClickFunction(MouseClickFunction func) {
		clickFunctions.add(func);
	}
	
	public void processOne() {
		MouseEvent e = clickEvents.remove();
		for(int i = 0; i < clickFunctions.size(); i++) {
			clickFunctions.get(i).accept(e);
		}
	}
	
	public void processAll() {
		while(!clickEvents.isEmpty()) {
			MouseEvent e = clickEvents.remove();
			for(int i = 0; i < clickFunctions.size(); i++) {
				clickFunctions.get(i).accept(e);
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		clickEvents.add(e);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		buttonStates[e.getButton()] = true;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		buttonStates[e.getButton()] = false;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		mouseInWindow = true;
	}

	@Override
	public void mouseExited(MouseEvent e) {
		mouseInWindow = false;
	}

}
