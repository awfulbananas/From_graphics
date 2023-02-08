package fromics;


import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.List;
import java.util.function.Consumer;
import java.util.Queue;
import java.awt.*;

//I didn't actually write most of this, so I'll put of commenting it for the most part until later
//you can probably mostly just use the key and codes lists, which contain the keys which are currently pressed
public class Keys extends KeyAdapter implements KeyListener{
	public Set<String> keys;
	public Set<Integer> codes;
	Queue<KeyEvent> typedEventQueue;
	Queue<KeyEvent> pressedEventQueue;
	public List<Consumer<KeyEvent>> typedCallbacks;
	public List<Consumer<KeyEvent>> pressedCallbacks;
	private boolean isShiftDown;
	
	public Keys() {
		keys = new HashSet<>();
		codes = new HashSet<>();
		typedEventQueue = new LinkedList<>();
		pressedEventQueue = new LinkedList<>();
		typedCallbacks = new LinkedList<>();
		pressedCallbacks = new LinkedList<>();
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(
			new KeyEventDispatcher() {
				public boolean dispatchKeyEvent(KeyEvent e) {
					isShiftDown = e.isShiftDown();
					return false;
				}
			});
	}
	
	public List<Consumer<KeyEvent>> getTriggerSet() {
		return typedCallbacks;
	}
	
	public void addTypedTrigger(Consumer<KeyEvent> trigger) {
		typedCallbacks.add(trigger);
	}
	
	public void addPressedTrigger(Consumer<KeyEvent> trigger) {
		pressedCallbacks.add(trigger);
	}
	
	public boolean isShiftDown() {
		return isShiftDown;
	}
	
	public void keyTyped(KeyEvent e) {
		typedEventQueue.add(e);
	}
	
	public void process() {
		while(!typedEventQueue.isEmpty()) {
			KeyEvent e = typedEventQueue.remove();
			for(Consumer<KeyEvent> c : typedCallbacks) {
				c.accept(e);
			}
		}
		while(!pressedEventQueue.isEmpty()) {
			KeyEvent e = pressedEventQueue.remove();
			for(Consumer<KeyEvent> c : pressedCallbacks) {
				c.accept(e);
			}
		}
	}
	
	public KeyEvent getNextTypedEvent() {
		return typedEventQueue.poll();
	}
	
	public KeyEvent getNextPressedEvent() {
		return pressedEventQueue.poll();
	}
	
	public Boolean hasTypedEvents() {
		return !typedEventQueue.isEmpty();
	}
	
	public Boolean hasPressedEvents() {
		return !pressedEventQueue.isEmpty();
	}
	
	public void processOne() {
		if(!typedEventQueue.isEmpty()) {
			KeyEvent e = typedEventQueue.remove();
			for(Consumer<KeyEvent> c : typedCallbacks) {
				c.accept(e);
			}
		}
		if(!pressedEventQueue.isEmpty()) {
			KeyEvent e = pressedEventQueue.remove();
			for(Consumer<KeyEvent> c : pressedCallbacks) {
				c.accept(e);
			}
		}
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		super.keyPressed(e);
//		System.out.println(e.getKeyCode());
		pressedEventQueue.add(e);
		if (!keys.contains(String.valueOf(e.getKeyChar()).toLowerCase())){
			keys.add(String.valueOf(e.getKeyChar()).toLowerCase());
		}
		if (!codes.contains(e.getKeyCode())){
			codes.add(e.getKeyCode());
		}
	}
	@Override
	public void keyReleased(KeyEvent e) {
		super.keyPressed(e);
		if (keys.contains(String.valueOf(e.getKeyChar()).toLowerCase())){
			keys.remove(String.valueOf(e.getKeyChar()).toLowerCase());
		}
		if (codes.contains((Integer)e.getKeyCode())){
			codes.remove((Integer)e.getKeyCode());
		}
	}
}
