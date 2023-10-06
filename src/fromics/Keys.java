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
	public Set<Integer> codes;
	Queue<Integer> typedCodeQueue;
	Queue<KeyEvent> pressedEventQueue;
	public List<KeypressFunction> typedCallbacks;
	public List<Consumer<KeyEvent>> pressedCallbacks;
	private boolean isShiftDown;
	
	public Keys() {
		codes = new HashSet<>();
		typedCodeQueue = new LinkedList<>();
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
	
	public List<KeypressFunction> getTriggerSet() {
		return typedCallbacks;
	}
	
	public void addTypedTrigger(KeypressFunction func) {
		typedCallbacks.add(func);
	}
	
	public void addPressedTrigger(Consumer<KeyEvent> trigger) {
		pressedCallbacks.add(trigger);
	}
	
	public boolean isShiftDown() {
		return isShiftDown;
	}
	
	public void keyTyped(KeyEvent e) {
		super.keyTyped(e);
	}
	
	public void process() {
		while(!typedCodeQueue.isEmpty()) {
			int e = typedCodeQueue.remove();
			for(KeypressFunction c : typedCallbacks) {
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
	
	public int getNextTypedEvent() {
		return typedCodeQueue.poll();
	}
	
	public KeyEvent getNextPressedEvent() {
		return pressedEventQueue.poll();
	}
	
	public Boolean hasTypedEvents() {
		return !typedCodeQueue.isEmpty();
	}
	
	public Boolean hasPressedEvents() {
		return !pressedEventQueue.isEmpty();
	}
	
	public void processOne() {
		if(!typedCodeQueue.isEmpty()) {
			int e = typedCodeQueue.remove();
			for(KeypressFunction c : typedCallbacks) {
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
		pressedEventQueue.add(e);
		if (!codes.contains(e.getKeyCode())){
			codes.add(e.getKeyCode());
		}
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		super.keyPressed(e);
		if (codes.contains((Integer)e.getKeyCode())){
			codes.remove((Integer)e.getKeyCode());
			typedCodeQueue.add(e.getKeyCode());
		}
	}
}
