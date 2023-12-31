package fromics;

import java.awt.event.KeyEvent;
import java.util.function.Consumer;

//represents function that will be run when any key is pressed,
//and will be given the KeyEvent object for when the key is released.
public interface KeypressFunction extends Consumer<KeyEvent> {}
