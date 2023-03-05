package fromics;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//a class representing a group of Linkables of a specific type
//Linkables of a different type can still be linked to it, but
//I'm not sure why you would ever do that
public class Group<E extends Linkable> extends Linkable implements Iterable<E>{
	//the list of Linkables of type E linked to this one
	protected List<E> linked;

	//constructs a new Group, with Linkable holder as it's parent
	//this Linkable has a different constructor because you'll
	//almost always want to save it in a variable
	public Group(Linkable holder) {
		super(0,0);
		linked = new ArrayList<>();
		holder.link(this);
	}
	
	//returns the number of children of type E
	//which also are of type className as given by 
	//Class.getCononicalName()
	public int count(String className) {
		int count = 0;
		for(E element : linked) {
			if(element.getClass().getCanonicalName().equals(className)) {
				count++;
			}
		}
		return count;
	}

	//draws this Linkable, draws nothing by default
	@Override
	protected void draw(Graphics g, double x, double y, double ang) {}

	//links a Linkable to this one, so that it follows it
	public void linkE(E child) {
		if(child != null) {
			child.parent = this;
			linked.add(child);
		}
	}
	
	//links Linkbale l to this Linkable, probably only works right if it's of type E
	//but the error correction might work
	@Override
	public void link(Linkable l) {
		try {
			linkE((E)l);
		} catch(IllegalArgumentException e) {
			super.link(l);
		}
	}
	
	//returns all linked children of type E
	public List<E> getLinkedE() {return linked;}
	
	//unlinks a Linkable of type E from this Group
	public void unlinkE(E child) {
		child.parent = null;
		linked.remove(child);
	}
	
	//unlinks Linkable l from this Group, probably only works if it's of type E
	@Override
	public void unlink(Linkable l) {
		try {
			unlinkE((E)l);
		} catch(IllegalArgumentException e) {
			super.unlink(l);
		}
	}

	//updates this Linkable and all of it's children of type E
	@Override
	public void updateAll() {
		update();
		Iterator<E> itr = linked.iterator();
		while(itr.hasNext()) {
			E next = itr.next();
			if(next.update()) {
				itr.remove();
			}
			
		}
	}
	
	//returns the number of children of type E
	@Override
	public int size() {
		return linked.size();
	}
	
	//draws all of this Group's children of type E
	//doesn't draw this Group by default, so if it should be drawn,
	//the override this method in addition to .draw()
	@Override
	public void drawAll(Graphics g) {
		for(Linkable l : linked) {
			l.drawAll(g);
		}
	}
	
	//returns an iterator for all this Group's children of type E
	@Override
	public Iterator<E> iterator() {
		return linked.iterator();
	}
	
	//returns a String representation of this group in the format
	//(x, y), [<linked1.toString()>, <linked2.toString()>, ... ,<linkedn.toString()>]
	@Override
	public String toString() {
		String s = super.toString() + ", [";
		for(E i : linked) {
			s += i.toString() +", ";
		}
		s = s.substring(0, s.length() - 2) + "]";
		return s;
	}
}
