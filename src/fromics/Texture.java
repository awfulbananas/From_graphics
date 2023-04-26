package fromics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.function.Function;

import javax.imageio.ImageIO;

//a class representing a texture using a raster image
//the intention is to link this to the Linkable which this is the texture for,
//rather than extending this Linkable
public class Texture extends Linkable{
	//an array of the Points referenced when checking the color to be used when rotating an image
	//as shown below.
	//  0
	//0 0 0
	//  0
	public static final Point[] REFERENCE_POINTS = {new Point(), new Point(0, 1), new Point(0, -1), new Point(-1, 0), new Point(1, 0)};
	//the inverse of the weight given to a point directly in the same place when rotating an image
	public static final double INVERSE_CENTER_POINT_WEIGHT = 1;
	
	//the image of this Texture
	protected BufferedImage img;
	//the size for this Texture to be drawn at
	protected double size;
	
	//constructs a new texture using Image img scaled to the given size
	public Texture(BufferedImage img, double size) {
		super(0, 0);
		this.img = img;
		this.size = size;
	}
	
	protected Texture() {
		super(0, 0);
	}
	
	//draws this texture using Graphics g
	@Override
	protected void draw(Graphics g, double xOff, double yOff, double angOff) {
		double totAng = angOff + ang;
		//if there's no rotation, don't do the rotation algorithm
		if(totAng == 0) {
			drawImg(g, img, xOff, yOff);
		}
		BufferedImage rotated = getRotatedImage(img, totAng);
		//draw the rotated image
		drawImg(g, rotated, xOff, yOff);
	}
	
	public static BufferedImage getRotatedImage(BufferedImage img, double ang) {
		
		if(ang % (Math.PI * 2) == 0) {
			return img;
		} else if(ang % (Math.PI / 2) == 0){
			return perpendicularRotation(img, (int)(ang / (Math.PI / 2)));
		} else {
			return generalRotation(img, ang);
		}
	}
	
	private static BufferedImage perpendicularRotation(BufferedImage img, int ang) {
		int width = img.getWidth();
		int height = img.getHeight();
		
		Function<Point, Point> newLocFunc;
		
		ang = ang % 4;
		
		BufferedImage newImg;
		if(ang == 1) {
			newImg = new BufferedImage(height, width, BufferedImage.TYPE_INT_ARGB);
			newLocFunc = (Point p) -> {
				return new Point(height - p.Y() - 1, p.X());
			};
		} else if(ang == 3) {
			newImg = new BufferedImage(height, width, BufferedImage.TYPE_INT_ARGB);
			newLocFunc = (Point p) -> {
				return new Point(p.Y(), width - p.X() - 1);
			};
		} else {
			newImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			newLocFunc = (Point p) -> {
				return new Point(width - p.X() - 1, height - p.Y() - 1);
			};
		}
		
		for(int i = 0; i < width; i++) {
			for(int j = 0; j < height; j++) {
				Point newLoc = newLocFunc.apply(new Point(i, j));
				newImg.setRGB((int)newLoc.X(), (int)newLoc.Y(), img.getRGB(i, j));
			}
		}
		
		return newImg;
	}
	
	private static BufferedImage generalRotation(BufferedImage img, double ang) {
		//calculate the unit vectors for the rotation
		Point tX = (new Point(1, 0)).rot(ang);
		Point tY = tX.getPerpendicular();
		//gets the width and height of the un-rotated image
		double oldWidth = img.getWidth();
		double oldHeight = img.getHeight();
		//calculates the new width and height of the image
		int newWidth = (int)Math.ceil(Math.abs(oldWidth * tX.X()) + Math.abs(oldHeight * tY.X()));
		int newHeight = (int)Math.ceil(Math.abs(oldWidth * tX.Y()) + Math.abs(oldHeight * tY.Y()));
		
		//creates a new image with the right size in INT_ARGB color space
		BufferedImage rotated = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
		//loop through every pixel of the new image
		for(int i = 0; i < newWidth; i++) {
			for(int j = 0; j < newHeight; j++) {
				//calculates the location this pixel would be if rotated back to the original
				Point referenceLoc = (new Point(i - newWidth / 2, j - newHeight / 2)).matrixTransform(tX, tY.copy()).add(oldWidth / 2, oldHeight / 2);
				
				//continues if this pixel would be outside of the old image
				if(referenceLoc.X() < 0 || referenceLoc.X() >= oldWidth || referenceLoc.Y() < 0 || referenceLoc.Y() >= oldHeight) {
					continue;
				}
				
				//gets the location of the nearest pixel in the original image from the referenceLoc
				int nearestX = (int)Math.round(referenceLoc.X());
				int nearestY = (int)Math.round(referenceLoc.Y());
				
				//get a weighted sum of the colors of nearby pixels to pixel (nearestX, nearestY)
				double weightSum = 0;
				double redSum = 0;
				double blueSum = 0;
				double greenSum = 0;
				double alphaSum = 0;
				for(int k = 0; k < REFERENCE_POINTS.length; k++) {
					Point curOff = REFERENCE_POINTS[k];
					Point curLoc = (new Point(nearestX, nearestY)).add(curOff);
					
					if(curLoc.X() < 0 || curLoc.Y() < 0 || curLoc.X() >= oldWidth || curLoc.Y() >= oldHeight) {
						continue;
					}
					
					//weight for each color is the inverse of the distance to the pixel from the referenceLoc
					double weight = 1 / (Math.pow(curLoc.X() - referenceLoc.X(), 2) + Math.pow(curLoc.Y() - referenceLoc.Y(), 2) + INVERSE_CENTER_POINT_WEIGHT);
					//if the pixel would be to far, it's excluded from the weighted sum entirely
					//the max distance is the square root of 2 because a unit square at any angle
					//around the referenceLoc would never include that pixel
					if(weight < 0.5) {
						continue;
					}
					Color c = new Color(img.getRGB((int)(nearestX + curOff.X()), (int)(nearestY + curOff.Y())), true);
					redSum += c.getRed() * weight;
					greenSum += c.getGreen() * weight;
					blueSum += c.getBlue() * weight;
					alphaSum += c.getAlpha() * weight;
					weightSum += weight;
				}
				
				Color finalColor = new Color((int)(redSum / weightSum), (int)(greenSum / weightSum), (int)(blueSum / weightSum), (int)(alphaSum / weightSum));
				rotated.setRGB(i, j, finalColor.getRGB());
			}
		}
		return rotated;
	}
	
	private void drawImg(Graphics g, BufferedImage img, double xOff, double yOff) {
		g.drawImage(img, (int)(xOff + X() - size * img.getWidth() / 2), 
				(int)(yOff + Y() - size * img.getHeight() / 2), 
				(int)(xOff + X() + size * img.getWidth() / 2), 
				(int)(yOff + Y() + size * img.getHeight() / 2), 
				0, 0, img.getWidth(), img.getHeight(), null);
	}
	
	public static BufferedImage loadImage(String pathName) {
		BufferedImage texture = null;
		try {
		    texture = ImageIO.read(new File(pathName));
		} catch (IOException e) {
			System.out.println("failed to load texture: " + pathName);
		}
		return texture;
	}
}
