package fromics;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * represents a group of screens to switch between, ie. different screens of a program, or sections of a menu
 * @author awfulbananas
 */
public abstract class Screens extends Background{
    /**
     * an array of the different screens to switch between.
     */
    protected Background[] screens;
    /**
     * the index of the current screen
     */
    protected int screen;

    /**
     * initializes a new Screens at (0, 0)
     */
    public Screens() {
        super();
    }

    /**
     * returns the currently selected screen
     * @returnthe currently selected screen
     */
    public Background currentScreen() {
        return screens[screen];
    }

    /**
     * updates this Screens object and the selected screen
     * @return whether this should be unlinked from its parent
     */
    @Override
    public boolean updateAll() {
        updating = true;
        boolean updateVal = update();
        screens[screen].updateAll();
        if(screens[screen].nextScreen()) {
            int nextScreen = screens[screen].getNextScreen();
            nextScreen = nextScreen==-1?(screen+1)%screens.length:nextScreen;
            screens[screen].close();
           setScreen(nextScreen);
        }
        updating = false;
        while(!linkQueue.isEmpty()) {
            link(linkQueue.remove());
        }
        while(!unlinkQueue.isEmpty()) {
            unlink(unlinkQueue.remove());
        }
        return updateVal;
    }

    /**
     * sets the current screen to the given index, closing the previous screen
     * and calling initScreen for the new index
     * @param newScreen the index of the screen to switch to
     */
    public void setScreen(int newScreen) {
        updating = true;
        if(screens[screen] != null) {
            screens[screen].close();
        }
        initScreen(newScreen);
        screen = newScreen;
        updating = false;
        link(screens[screen]);
        updating = true;
    }

    /**
     * initializes the screen at the given index.
     * I usually implement this with a switch statement, but as long as after this is called
     * the Background at screens[n] is non-null and ready to be used any implementation should be fine
     * @param n the index of the screen to initialize
     */
    protected abstract void initScreen(int n);

    /**
     * draws this Screens and the current screen
     * @param g the Graphics object to draw with
     * @param img the image to draw on
     */
    public void drawAll(Graphics g, BufferedImage img) {
        draw(g, img, 0, 0, 0);
        if(screens[screen] == null) return;
        screens[screen].drawAll(g, img);
    }
}
