package fromics;

import java.awt.event.MouseEvent;
import java.util.function.Consumer;

//represents function that will be run when any mouse button is clicked,
//and will be given the MouseEvent object for when the mouse click is triggered.
public interface MouseClickFunction extends Consumer<MouseEvent> {}
