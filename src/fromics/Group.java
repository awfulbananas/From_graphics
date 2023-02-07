package fromics;

import java.awt.Graphics;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Group<E extends Linkable> extends Linkable implements Iterable<E>{
	protected List<E> linked;

	public Group(Linkable holder) {
		super(0,0);
		linked = new LinkedList<>();
		holder.link(this);
	}
	
	public int count(String className) {
		int count = 0;
		for(E element : linked) {
			if(element.getClass().getCanonicalName().equals(className)) {
				count++;
			}
		}
		return count;
	}

	@Override
	protected void draw(Graphics g, double x, double y, double ang) {}

	@Override
	public boolean isValid() {
		return true;
	}
	
	//links a Linkable to this one, so that it follows it
	public void linkE(E child) {
		if(child != null) {
			child.parent = this;
			linked.add(child);
		}
	}
	
	//returns all linked children in  List
	public List<E> getLinkedE() {return linked;}
	
	//unlinks a Linkable from this Linkable
	public void unlinkE(E child) {
		child.parent = null;
		linked.remove(child);
	}

	@Override
	public boolean update() {
		for(E l : linked) {
			l.update();
		}
		return false;
	}
	
	@Override
	public int size() {
		return linked.size();
	}


	@Override
	public Linkable parent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void drawAll(Graphics g) {
		for(E l : linked) {
			l.drawAll(g);
		}
	}

	@Override
	public Iterator<E> iterator() {
		return linked.iterator();
	}
}
