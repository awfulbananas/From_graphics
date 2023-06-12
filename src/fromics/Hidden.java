package fromics;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Set;
import java.util.HashSet;

public class Hidden extends Linkable {
	private Set<Linkable> shown;

	public Hidden(double x, double y) {
		super(x, y);
		shown = new HashSet<>();
	}
	public Hidden(double x, double y, double z) {
		super(x, y, z);
		shown = new HashSet<>();
	}
	
	public void show(Linkable l) {
		if(!linked.contains(l)) {
			link(l);
		}
		shown.add(l);
	}
	
	public void hide(Linkable l) {
		shown.remove(l);
	}
	
	public void drawAll(Graphics g) {
		setDefColor(g);
		draw(g, parent.getAbsX(), parent.getAbsY(), parent.getAbsAng());
		for(Linkable l : linked) {
			if(shown.contains(l)) {
				l.drawAll(g);
			}
		}
	}

	@Override
	protected void draw(Graphics g, double xOff, double yOff, double angOff) {
		
	}

}
