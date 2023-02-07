package fromics;


import java.awt.MouseInfo;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.function.Consumer;

public class Mouse implements MouseListener {
	private Set<Integer> buttons;
	private Set<Consumer<MouseEvent>> clickedCallbacks;
	private Queue<MouseEvent> eventQueue;
	
	public Mouse() {
		buttons = new HashSet<Integer>();
		clickedCallbacks = new HashSet<>();
		eventQueue = new LinkedList<>();
	}
	
	public Set<Consumer<MouseEvent>> getCallbackSet() {
		return clickedCallbacks;
	}
	
	public int getX() {
		return MouseInfo.getPointerInfo().getLocation().x;
	}
	
	public int getY() {
		return MouseInfo.getPointerInfo().getLocation().y;
	}
	
	public Set<Integer> getMButtons() {
		return buttons;
	}
	
	public Queue<MouseEvent> getEvents() {
		Queue<MouseEvent> oldQueue = eventQueue;
		eventQueue = new LinkedList<>();
		return oldQueue;
	}
	
	public MouseEvent getNextEvent() {
		return eventQueue.remove();
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		eventQueue.add(e);
//		System.out.println(e);
//		System.out.println(eventQueue);
	}
	
	public void process() {
		while(!eventQueue.isEmpty()) {
			MouseEvent e = eventQueue.remove();
			for(Consumer<MouseEvent> c : clickedCallbacks) {
				c.accept(e);
			}
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		System.out.println(e.getButton());
		buttons.remove((Integer)e.getButton());
	}
	@Override
	public void mousePressed(MouseEvent e) {
		System.out.println(e);
		if (!buttons.contains(e.getButton())) {
			buttons.add(e.getButton());
//			System.out.println(String.valueOf(e.getButton()));
		}
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
