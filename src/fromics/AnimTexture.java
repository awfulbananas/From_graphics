package fromics;

import java.awt.image.BufferedImage;

public class AnimTexture extends Texture {
	
	private final int updateTime;
	private int updateTimer;
	private int imgIndex;
	private BufferedImage[] imgs;
	
	public AnimTexture(BufferedImage[] imgs, double size, int updateTime) {
		img = imgs[0];
		this.imgs = imgs;
		imgIndex = 0;
		this.size = size;
		this.updateTime = updateTime;
		updateTimer = updateTime;
	}
	
	public boolean update() {
		if(updateTimer-- == 0) {
			updateTimer = updateTime;
			imgIndex = (imgIndex + 1) % imgs.length;
			img = imgs[imgIndex];
		}
		return false;
	}
}
