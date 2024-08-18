package fromics;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;
import java.util.List;

public class SelectionField<E> extends InputField<E>{
    private int selected;
    private final List<E> options;

    public SelectionField(List<E> items, double x, double y, Font f) {
        super(x, y, f);
        this.options = items;
    }

    public void onFirstLink() {
        addKeystrokeFunction((KeyEvent e) -> {
            switch(e.getKeyCode()) {
                case KeyEvent.VK_UP:
                    selected = (selected + options.size() - 1) % options.size();
                    break;
                case KeyEvent.VK_DOWN:
                    selected = (selected + 1) % options.size();
                    break;
            }
        });
    }

    @Override
    public E getData() {
        return options.get(selected);
    }

    @Override
    protected void draw(Graphics g, BufferedImage img, double xOff, double yOff, double angOff) {
        g.setFont(font);
        FontRenderContext frc = g.getFontMetrics().getFontRenderContext();
        int totX = (int)(X() + xOff);
        int fontHeight = (int)font.getStringBounds("I", frc).getHeight();
        int totY = (int)(Y() + yOff) + fontHeight + 5;
        for(int i = 0; i < options.size(); i++) {
            g.drawString(options.get(i).toString(), totX, totY + i * (fontHeight + 10));
            if(i == selected) {
                int strWidth = (int)font.getStringBounds(options.get(i).toString(), frc).getWidth();
                g.drawRect(totX - 5, totY - fontHeight + i *(fontHeight + 10), strWidth + 10, fontHeight + 5);
            }
        }
    }
}
