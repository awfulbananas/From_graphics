package fromics;

import java.awt.*;
import java.awt.image.BufferedImage;

//represents a group of screens that you can switch between
public abstract class Screens extends Background{
    protected Background[] screens;
    protected int screen;

    public Screens() {
        super();
    }

    public Background currentScreen() {
        return screens[screen];
    }

    @Override
    public boolean updateAll() {
        boolean updateVal = update();
        if(screens[screen].nextScreen()) {
            int nextScreen = screens[screen].getNextScreen();
            nextScreen = nextScreen==-1?(screen+1)%screens.length:nextScreen;
            screens[screen].close();
            initScreen(nextScreen);
            screen = nextScreen;
            link(screens[screen]);
        }
        screens[screen].updateAll();
        return updateVal;
    }

    public void setScreen(int newScreen) {
        screens[screen].close();
        screen = newScreen;
        initScreen(screen);
        link(screens[screen]);
    }

    protected abstract void initScreen(int n);

    public void drawAll(Graphics g, BufferedImage img) {
        draw(g, img, 0, 0, 0);
        if(screens[screen] == null) return;
        screens[screen].drawAll(g, img);
    }
}
