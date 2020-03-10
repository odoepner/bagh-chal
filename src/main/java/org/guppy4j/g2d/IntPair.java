package org.guppy4j.g2d;

/**
 * Immutable alternative to the AWT Dimension class
 */
public final class IntPair implements Size {

    private final int x;
    private final int y;

    public IntPair(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        return isSameAs((IntPair) other);
    }

    @Override
    public int hashCode() {
        return 31 * x + y;
    }
}
