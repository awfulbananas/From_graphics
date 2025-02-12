package fromics;

import java.awt.IllegalComponentStateException;
import java.awt.MouseInfo;
import java.awt.event.*;
import java.util.*;

public class Mouse extends MouseAdapter implements MouseListener, MouseWheelListener {
	public int DRAG_CODE_OFFSET = 10;

	private final Frindow win;
	private final Queue<MouseEvent> mouseEventQueue;
	private final List<MouseEventFunction> mouseEventFunctions;
	private final Set<Integer> codes;
	private boolean mouseInWindow;
	
	public Mouse(Frindow win) {
		this.win = win;
		mouseInWindow = true;
		mouseEventQueue = new LinkedList<>();
		mouseEventFunctions = new ArrayList<>();
		codes = new HashSet<>();
	}
	
	public Point getMouseLoc() {
		try {
			return win.locOnScreenFromGlobalLoc(new Point(MouseInfo.getPointerInfo().getLocation().x, MouseInfo.getPointerInfo().getLocation().y));
		} catch(IllegalComponentStateException e) {
			return new Point();
		}
	}

	public boolean getMouseButton(int i) {
		return codes.contains(i);
	}
	
	public boolean getMousePresent() {
		return mouseInWindow;
	}
	
	public void addMouseEventFunction(MouseEventFunction func) {
		if(!mouseEventFunctions.contains(func)) {
			mouseEventFunctions.add(func);
		}
	}

	private void process(MouseEvent e) {
		for(int i = 0; i < mouseEventFunctions.size(); i++) {
			mouseEventFunctions.get(i).accept(e);
		}
	}
	
	public void processOne() {
		if(!mouseEventQueue.isEmpty()) {
			MouseEvent e = mouseEventQueue.remove();
			process(e);
		}
	}
	
	public void process() {
		loop:
		while(!mouseEventQueue.isEmpty()) {
			try {
				MouseEvent e = mouseEventQueue.remove();
				process(e);
			} catch (NoSuchElementException e) {
				System.out.println("WTF java");
				e.printStackTrace();
				break loop;
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		super.mouseClicked(e);
		mouseEventQueue.add(e);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		super.mousePressed(e);
		codes.add(e.getButton());
		mouseEventQueue.add(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		super.mouseReleased(e);
		codes.remove(e.getButton());
		mouseEventQueue.add(e);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		super.mouseEntered(e);
		mouseInWindow = true;
		mouseEventQueue.add(e);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		super.mouseExited(e);
		mouseInWindow = false;
		mouseEventQueue.add(e);
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		super.mouseWheelMoved(e);
		mouseEventQueue.add(e);
		mouseEventQueue.add(e);
	}

	public static boolean buttonDownInEvent(MouseEvent e, int button) {
		int mask = MouseEvent.getMaskForButton(button);
		return (mask & e.getModifiersEx()) == mask;
	}

}
