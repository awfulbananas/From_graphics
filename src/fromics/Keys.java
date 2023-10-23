package fromics;


import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.List;
import java.util.Queue;

//a class which manages key events and keeps track of which keys are being held
public class Keys extends KeyAdapter implements KeyListener{
	//a Set containing the key codes for every key which is currently being pressed
	public Set<Integer> codes;
	//a queue of key codes for key released events in chronological order
	//used to determine when a key has been typed and if a key is being held
	Queue<Integer> typedCodeQueue;
	//a List of all functions to be run whenever a key is typed
	public List<KeypressFunction> keypressFunctions;
	
	public Keys() {
		codes = new HashSet<>();
		typedCodeQueue = new LinkedList<>();
		keypressFunctions = new LinkedList<>();
	}
	
	//returns a list of all KeypressFunctions which are called whenever a key is typed
	public List<KeypressFunction> getkeypressFunctions() {
		return keypressFunctions;
	}
	
	//adds a new KeypressFunction to be called when a key is tyed
	public void addKeypressFunction(KeypressFunction func) {
		if(!keypressFunctions.contains(func))
			keypressFunctions.add(func);
	}
	
	//processes all currently queued key typed codes, running relevant KeypressFunctions
	public void process() {
		while(!typedCodeQueue.isEmpty()) {
			int e = typedCodeQueue.remove();
			for(KeypressFunction c : keypressFunctions) {
				c.accept(e);
			}
		}
	}
	
	//processes only the oldest keypress event, running relevant KeypressFunctions
	public void processOne() {
		if(!typedCodeQueue.isEmpty()) {
			int e = typedCodeQueue.remove();
			for(KeypressFunction c : keypressFunctions) {
				c.accept(e);
			}
		}
	}
	
	//adds a key to the queue of pressed key events whenever a key is pressed
	@Override
	public void keyPressed(KeyEvent e) {
		super.keyPressed(e);
		if (!codes.contains(e.getKeyCode())){
			codes.add(e.getKeyCode());
		}
	}
	
	//adds a key to the queue of released key events whenever a key is released
	@Override
	public void keyReleased(KeyEvent e) {
		super.keyPressed(e);
		if (codes.contains((Integer)e.getKeyCode())){
			codes.remove((Integer)e.getKeyCode());
			typedCodeQueue.add(e.getKeyCode());
		}
	}
}
