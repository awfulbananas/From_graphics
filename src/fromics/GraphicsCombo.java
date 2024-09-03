package fromics;


import java.awt.*;
import java.awt.image.BufferedImage;

public class GraphicsCombo {
    private final Graphics g;
    private final BufferedImage img;

    public GraphicsCombo(BufferedImage source) {
        img = source;
        g = source.getGraphics();
    }

    public Graphics getG() {
        return g;
    }

    public BufferedImage getImg() {
        return img;
    }
}
