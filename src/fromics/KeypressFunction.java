package fromics;

import java.util.function.IntConsumer;

//represents function that will be run when any key is pressed,
//and will be given the KeyEvent key code for when the key is released
public interface KeypressFunction extends IntConsumer {}
