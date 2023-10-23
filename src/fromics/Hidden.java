package fromics;

import java.awt.Graphics;
import java.util.Set;
import java.util.HashSet;

//a Linkable which can be used to conveniently show or hide linked Linkables
//it may be useful for a camera object where you want to show or hide different layers
//or aspects of a game or application at a timne
public class Hidden extends Linkable {
	//a Set of all the Linkables linked to this Hidden which are currently shown
	private Set<Linkable> shown;

	//constructs a new Hidden with the given x and y positions
	public Hidden(double x, double y) {
		super(x, y);
		shown = new HashSet<>();
	}
	
	//constructs a new Hidden with the given x, y, and z positions
	public Hidden(double x, double y, double z) {
		super(x, y, z);
		shown = new HashSet<>();
	}
	
	//begins displaying the given Linkable, linking it if it isn't already
	public void show(Linkable l) {
		if(!linked.contains(l)) {
			link(l);
		}
		shown.add(l);
	}
	
	//stops displaying the given Linkable, or does nothing if it isn't
	//already displayed
	public void hide(Linkable l) {
		shown.remove(l);
	}
	
	//draws all of the Linkables which should currently be shown by this Hidden
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
