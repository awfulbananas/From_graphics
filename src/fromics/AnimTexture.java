package fromics;

import java.awt.image.BufferedImage;

//a class representing an animated Texture
public class AnimTexture extends Texture {
	//the amount of time per update of the texture
	private final int updateTime;
	//the amount of time until the texture should be updated
	private double updateTimer;
	//the index of the currently shown image
	private int imgIndex;
	//the array of images to be drawn
	private BufferedImage[] imgs;
	
	//constructs a new AnimTexture with the given images, size, and update time
	public AnimTexture(BufferedImage[] imgs, double size, int updateTime) {
		img = imgs[0];
		this.imgs = imgs;
		imgIndex = 0;
		this.size = size;
		this.updateTime = updateTime;
		updateTimer = updateTime;
	}
	
	//updates this animTexture, decrementing the timer and changing the texture
	//and resetting the timer if it reaches zero
	public boolean update() {
		if((updateTimer -= dt()) == 0) {
			updateTimer = updateTime;
			imgIndex = (imgIndex + 1) % imgs.length;
			img = imgs[imgIndex];
		}
		return false;
	}
}
