package files;

import java.awt.Color;
import java.awt.Graphics;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

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
	public static final int DRAW_DETAIL = 1000;
	
	private BezierSpline[] splines;
	private double size;
	private boolean distanceCalculated;
	private double distance;
	
	
	
	public SplinePath(double x, double y, Color c, BezierSpline[] splines) {
		super(x, y);
		setColor(c);
		this.splines = splines;
		distanceCalculated = false;
		distance = 0;
		size = 1;
	}
	
	private String formatPathData(String pathData) {
		char[] oldString = pathData.toCharArray();
		StringBuilder newString = new StringBuilder(oldString.length);
		boolean readingDivider = false;
		boolean firstArg = true;
		size = 1;
		for(int i = 0; i < oldString.length; i++) {
			char current = oldString[i];
			if(Character.isDigit(current) || current == '.') {
				if(readingDivider && !firstArg) newString.append(", ");
				readingDivider = false;
				firstArg = false;
				newString.append(current);
			} else if(Character.isAlphabetic(current)) {
				newString.append('\n');
				readingDivider = false;
				firstArg = true;
				newString.append(current);
			} else if(current == '-') {
				readingDivider = false;
				if(!firstArg) newString.append(", ");
				firstArg = false;
				newString.append("-");
			} else {
				readingDivider = true;
			}
		}
		newString.deleteCharAt(0);
		
		return newString.toString();
	}
	
	//constructs a new SplinePath with the given path data and color
	//the path data must be in the format where each command is on it's own line
	//and each line has a command, and each command is directly followed by all of the 
	//arguments, which should each be separated by ", "
	//thows an IllegalArgumentException if pathData is incorrectly formatted
	public SplinePath(String pathData, Color c) {
		super(0, 0);
		
		pathData = formatPathData(pathData);
		
		String[] allCurves = pathData.split("\n");
		List<BezierSpline> splines = new ArrayList<>();
		BezierSpline prevSpline = null;
		Point prevLoc = new Point(0, 0);
		Point firstPoint = null;
		
		System.out.println(Arrays.toString(allCurves));
		
		for(String curveString : allCurves) {
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
				
				double[] argDoubles = new double[argStrings.length];
				for(int i = 0; i < argDoubles.length; i++) {
					argDoubles[i] = Double.parseDouble(argStrings[i]);
				}
				
				int argsUsed;
				
				switch(Character.toLowerCase(command)) {
					case('m'):
						argsUsed = 2;
						break;
					case('l'):
						argsUsed = 2;
						break;
					case('c'):
						argsUsed = 6;
						break;
					default:
						argsUsed = 1;
						break;
				}
				
				boolean isAbsolute = Character.isUpperCase(command);
				
				int i = 0;
				do {
					Point origin = isAbsolute ? new Point(0, 0) : prevLoc;
					
					switch(Character.toLowerCase(command)) {
						case('m'):{
							prevSpline = null;
							prevLoc = origin.add(new Point(argDoubles[i], argDoubles[i + 1]));
							firstPoint = prevLoc;
							break;
						} case('l'):{
							Point[] controlPoints = new Point[2];
							controlPoints[0] = prevLoc.copy();
							controlPoints[1] = origin.copy().add(argDoubles[i], argDoubles[i + 1]);
							BezierSpline newSpline = new BezierSpline(controlPoints);
							splines.add(newSpline);
							prevSpline = newSpline;
							prevLoc = controlPoints[1];
							break;
						} case('h'):{
							Point[] controlPoints = new Point[2];
							controlPoints[0] = prevLoc.copy();
							controlPoints[1] = origin.copy().add(argDoubles[i], 0);
							
							BezierSpline newSpline = new BezierSpline(controlPoints);
							splines.add(newSpline);
							prevSpline = newSpline;
							prevLoc = controlPoints[1];
							break;
						} case('v'):{
							Point[] controlPoints = new Point[2];
							controlPoints[0] = prevLoc.copy();
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
							Point[] splineArgs = new Point[4];
							splineArgs[0] = prevLoc.copy();
							for(int j = 0; j < 3; j += 1) {
								splineArgs[j + 1] = new Point(argDoubles[i + 2 * j] + origin.X(), argDoubles[i + 1 + 2 * j] + origin.Y());
							}
							BezierSpline newSpline = new BezierSpline(splineArgs);
							splines.add(newSpline);
							prevSpline = newSpline;
							prevLoc = newSpline.getEnd();
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
		
		System.out.println(Arrays.toString(this.splines));
	}
	
	public SplinePath(File splineFile, Color c) {
		super(0, 0);
		setColor(c);
		Scanner sIn;
		try {
			sIn = new Scanner(splineFile);
		} catch (FileNotFoundException e) {
			sIn = null;
		}
		String[] originStrings = sIn.nextLine().split(",");
		Point origin = new Point(Double.parseDouble(originStrings[0]), Double.parseDouble(originStrings[1]));
		Point prevLoc = origin;
		List<BezierSpline> splineList = new ArrayList<>();
		boolean reading = true;
		while(reading) {
			String[] argStrings = sIn.nextLine().split(",");
			if(argStrings[0].equals("end")) {
				reading = false;
				break;
			}
			double[] argDoubles = new double[argStrings.length];
			for(int i = 0; i < argDoubles.length; i++) {
				String s = argStrings[i];
				if(s.equals("end")) {
					reading = false;
					break;
				}
				argDoubles[i] = Double.parseDouble(argStrings[i]);
			}
			
			Point[] splineArgs = new Point[1 + (argDoubles.length + 1) / 2];
			splineArgs[0] = prevLoc.copy();
			for(int j = 0; j < splineArgs.length - 1; j += 1) {
				splineArgs[j + 1] = new Point(argDoubles[2 * j], argDoubles[1 + 2 * j]);
			}
			BezierSpline newSpline = new BezierSpline(splineArgs);
			splineList.add(newSpline);
			prevLoc = newSpline.getEnd();
		}
		splines = new BezierSpline[splineList.size()];
		for(int i = 0; i < splines.length; i++) {
			splines[i] = splineList.get(i);
		}
	}
	
	public void setSize(double size) {
		this.size = size;
	}
	
	public Point[] getEvenPointArr(double distDiff) {
		return getEvenPointArr(distDiff, 0, 0);
	}
	
	public Point[] getEvenPointArr(double distDiff, double xOff, double yOff) {
		if(!distanceCalculated) {
			calculateDistance();
		}
		Point[] points = new Point[(int)(distance / distDiff) + 1];
		//loop through every spline, keeping track of the distance overshot of the previous spline
		double distOver = 0;
		int j = 0;
		for(int i = 0; i < splines.length; i++) {
			BezierSpline current = splines[i];
			//loop through distances of the current spline
			//separated by the distance difference, also keeping track
			//of the current point index
			double currLength = current.calculateDistances();
			double dist = distOver;
			while(dist < currLength) {
				points[j] = current.getAtDist(dist).mult(size).add(xOff, yOff);
				dist += distDiff; 
				j++;
			}
			distOver = dist - currLength;
		}
		return points;
	}
	
	public Point[] getUnevenPointArr(double tDiff, double xOff, double yOff) {
		Point[] points = new Point[(int)(splines.length / tDiff)];
		System.out.println(points.length);
		//loop through every spline, keeping track of the distance overshot of the previous spline
		double tOver = 0;
		int j = 0;
		System.out.println(points.length);
		for(int i = 0; i < splines.length; i++) {
			BezierSpline current = splines[i];
			//loop through distances of the current spline
			//separated by the distance difference, also keeping track
			//of the current point index
			double t = 0;
			while(t <= 1.0) {
				points[j] = current.get(t).mult(size).add(xOff, yOff);
				t += tDiff; 
				j++;
			}
		}
		System.out.println("done");
		return points;
	}
	
	private void calculateDistance() {
		distance = 0;
		for(BezierSpline s : splines) {
			distance += s.calculateDistances();
		}
	}
	
	@Override
	protected void draw(Graphics g, double xOff, double yOff, double angOff) {
		final double diff = 1.0 / DRAW_DETAIL;
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
