package fromics;

public enum Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT;

    public final int NUM_DIRS = 4;

    public int val() {
        return switch (this) {
            case RIGHT -> 0;
            case UP -> 1;
            case LEFT -> 2;
            case DOWN -> 3;
        };
    }

    public static Direction intDir(int d) {
        return switch (d) {
            case 0 -> RIGHT;
            case 1 -> UP;
            case 2 -> LEFT;
            case 3 -> DOWN;
            default -> null;
        };
    }

    public Direction clockwise() {
        return switch (this) {
            case UP -> RIGHT;
            case RIGHT -> DOWN;
            case DOWN -> LEFT;
            case LEFT -> UP;
        };
    }

    public Direction counterClockwise() {
        return switch(this) {
            case RIGHT -> UP;
            case UP -> LEFT;
            case LEFT -> DOWN;
            case DOWN -> RIGHT;
        };
    }

    public Direction flip() {
        return switch (this) {
            case RIGHT -> LEFT;
            case LEFT -> RIGHT;
            case UP -> DOWN;
            case DOWN -> UP;
        };
    }

    public static <E> E getDirFrom(E[][] arr, int x, int y, Direction dir) {
        return switch (dir) {
            case null -> arr[x % arr.length][y % arr[0].length];
            case UP -> arr[x % arr.length][(y + arr[0].length - 1) % arr[0].length];
            case DOWN -> arr[x % arr.length][(y + 1) % arr.length];
            case LEFT -> arr[(x + arr.length - 1) % arr.length][y % arr.length];
            case RIGHT -> arr[(x + 1) % arr.length][y % arr.length];
        };
    }

    @Override
    public String toString() {
        return switch (this) {
            case RIGHT -> "RIGHT";
            case LEFT -> "LEFT";
            case UP -> "UP";
            case DOWN -> "DOWN";
        };
    }

    public static Direction parseDirection(String s) {
        return switch (s.trim().toLowerCase()) {
            case "up" -> UP;
            case "down" -> DOWN;
            case "left" -> LEFT;
            case "right" -> RIGHT;
            default -> null;
        };
    }
}
