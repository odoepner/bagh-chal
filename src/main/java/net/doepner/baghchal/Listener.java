package net.doepner.baghchal;

import net.doepner.baghchal.model.Piece;

/**
 * Listens to game table events
 */
public interface Listener {

    void afterJump(Piece piece);

    void afterStep(Piece piece);

    Listener NONE = new Listener() {

        @Override
        public void afterJump(Piece piece) {
            // ignore
        }

        @Override
        public void afterStep(Piece piece) {
            // ignore
        }

    };
}
