package files;

import java.awt.Color;
import java.awt.Graphics;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import fromics.Linkable;
import fromics.Point;

//represents a path made of splines, often used in svgs
//svg paths are composed of a series of instructions for a pen which are
//	m, move the pen to the following coordinates
//	l, draw a line to the following coordinates
//	h, draw a horizontal line for the given distance
//	v, draw a vertical line for the given distance
//	z, draw a straight line to the beginning of the path
//	c, draw a bezier curve with the following n points
//	s, draw a bezier curve where the first two points are the current pen location
//		and the reflection of the previous bezier point, and the following points are
//		the n points following the command
//		not currently implemented
//	q, draws a quadratic bezier curve where points 2 & 3 share a location as the first
//		argument and the last point is the second argument
//	t, draws a bezier curve which is a reflection of the previous bezier curve
//		not currently implemented
//	a, weird nonsense with ovals, which I'm not going to implement right now
//all commands use relative coordinates for lowercase and absolute for uppercase,
//but all commands are saved in absolute coordinates as splines
public class SplinePath extends Linkable {
	public static final int DRAW_DETAIL = 100;
	
	private Color color;
	private BezierSpline[] splines;
	private double size;
	
	public SplinePath(double x, double y, Color c, BezierSpline[] splines) {
		super(x, y);
		this.color = c;
		this.splines = splines;
	}
	
	//constructs a new SplinePath with the given path data and color
	//the path data must be in the format where each command is on it's own line
	//and each line has a command, and each command is directly followed by all of the 
	//arguments, which should each be separated by ", "
	//thows an IllegalArgumentException if pathData is incorrectly formatted
	public SplinePath(String pathData, Color c) {
		super(0, 0);
		
		pathData = pathData.replace("m","\nm");
		pathData = pathData.replace("l","\nl");
		pathData = pathData.replace("h","\nh");
		pathData = pathData.replace("v","\nv");
		pathData = pathData.replace("z","\nz");
		pathData = pathData.replace("c","\nc");
		pathData = pathData.replace("s","\ns");
		pathData = pathData.replace("q","\nq");
		pathData = pathData.replace(" ",", ");
		pathData = pathData.replace("-",", -");
		
		String[] allCurves = pathData.split("\n");
		List<BezierSpline> splines = new ArrayList<>();
		BezierSpline prevSpline = null;
		Point prevLoc = new Point(0, 0);
		Point firstPoint = null;
		for(String curveString : allCurves) {
			System.out.println(curveString);
			if(curveString.length() > 0) {
				char command = curveString.charAt(0);
				String commandArgs = curveString.substring(1);
				if(commandArgs.startsWith(", ")) commandArgs = commandArgs.substring(2);
				String[] argStrings;
				if(commandArgs.equals("")) {
					argStrings = new String[0];
				} else {
					argStrings = commandArgs.split(", ");
				}
				System.out.println(Arrays.toString(argStrings));
				
				double[] argDoubles = new double[argStrings.length];
				for(int i = 0; i < argDoubles.length; i++) {
					argDoubles[i] = Double.parseDouble(argStrings[i]);
				}
				
				Point origin = new Point(0, 0);
				int argsUsed = 0;
				
				if(command == 'h' || command == 'v') argsUsed = 1;
				if(command == 'm' || command == 'l') argsUsed = 2;
				if(command == 'c') argsUsed = 6;
				
				if(!Character.isUpperCase(command)) {
					origin = prevLoc;
				}
				
				int i = 0;
				do {
					switch(Character.toLowerCase(command)) {
						case('m'):{
							prevSpline = null;
							prevLoc = origin.add(new Point(argDoubles[i], argDoubles[i + 1]));
							firstPoint = prevLoc;
							splines.add(null);
							break;
						} case('l'):{
							Point[] controlPoints = new Point[2];
							controlPoints[0] = origin.copy();
							controlPoints[1] = origin.copy().add(argDoubles[i], argDoubles[i + 1]);
							BezierSpline newSpline = new BezierSpline(controlPoints);
							splines.add(newSpline);
							prevSpline = newSpline;
							prevLoc = controlPoints[1];
							break;
						} case('h'):{
							Point[] controlPoints = new Point[2];
							controlPoints[0] = origin.copy();
							controlPoints[1] = origin.copy().add(argDoubles[i], 0);
							
							BezierSpline newSpline = new BezierSpline(controlPoints);
							splines.add(newSpline);
							prevSpline = newSpline;
							prevLoc = controlPoints[1];
							break;
						} case('v'):{
							Point[] controlPoints = new Point[2];
							controlPoints[0] = origin.copy();
							controlPoints[1] = origin.copy().add(0, argDoubles[i]);
							BezierSpline newSpline = new BezierSpline(controlPoints);
							splines.add(newSpline);
							prevSpline = newSpline;
							prevLoc = controlPoints[1];
							break;
						} case('z'):{
							Point[] controlPoints = new Point[2];
							controlPoints[0] = prevLoc.copy();
							controlPoints[1] = firstPoint.copy();
							BezierSpline newSpline = new BezierSpline(controlPoints);
							splines.add(newSpline);
							prevSpline = newSpline;
							prevLoc = controlPoints[1];
							break;
						} case('c'):{
							System.out.println("--"+Arrays.toString(argDoubles));
							System.out.println(argDoubles.length);
							System.out.println(prevLoc);
							Point[] splineArgs = new Point[4];
							splineArgs[0] = origin.copy();
							for(int j = 0; j < 3; j += 1) {
								splineArgs[j + 1] = new Point(argDoubles[i + 2 * j] + origin.X(), argDoubles[i + 1 + 2 * j] + origin.Y());
							}
							System.out.println(Arrays.toString(splineArgs));
							BezierSpline newSpline = new BezierSpline(splineArgs);
							splines.add(newSpline);
							prevSpline = newSpline;
							origin = newSpline.getEnd();
							break;
						} default:{
							throw new IllegalArgumentException();
						}
					}
					i += argsUsed;
				} while(i < argDoubles.length);
				if(firstPoint == null) firstPoint = prevSpline.getStart().copy();
			}
		}
		
		BezierSpline[] splineArr = new BezierSpline[splines.size()];
		for(int i = 0; i < splineArr.length; i++) {
			splineArr[i] = splines.get(i);
		}
		this.splines = splineArr;
	}
	
	public void setColor(Color c) {
		this.color = c;
	}
	
	public void setSize(double size) {
		this.size = size;
	}
	
	@Override
	protected void draw(Graphics g, double xOff, double yOff, double angOff) {
		final double diff = 1.0 / DRAW_DETAIL;
		g.setColor(color);
		int index = 1;
		BezierSpline first = splines[0];
		while(first == null) {
			first = splines[index];
			index++;
		}
		Point start = first.getStart().mult(size).add(xOff, yOff).add(this);
		Point end = null;
		for(double t = 0; t <= splines.length; t += diff) {
			BezierSpline current = splines[(int)t];
			if(current != null) {
				end = current.get(t % 1).copy().mult(size).add(xOff, yOff).add(this);
				g.drawLine((int)start.X(), (int)start.Y(), (int)end.X(), (int)end.Y());
				start = end;
			} else {
				t++;
				start = splines[(int)t].getStart().mult(size).add(xOff, yOff).add(this);
			}
		}
	}
	
}
