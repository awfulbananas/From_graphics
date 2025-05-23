package fromics.events;

public interface Event {
    public boolean isFinished();
    public void start();
    public void act();
}
