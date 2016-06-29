package net.doepner.baghchal;

import net.doepner.baghchal.model.Board;
import net.doepner.baghchal.model.Position;

import static net.doepner.baghchal.model.Piece.PREDATOR;
import static net.doepner.baghchal.model.Piece.PREY;

/**
 * Sets up the pieces on the board
 */
public final class BoardSetup {

    public void setup(Board board) {
        final Position p1 = board.getTopLeft();
        final Position p2 = board.getBottomRight();

        board.set(p1, PREDATOR);
        board.set(p1.x(), p2.y(), PREDATOR);
        board.set(p2.x(), p1.y(), PREDATOR);
        board.set(p2, PREDATOR);

        for (int x = p1.x(); x <= p2.x(); x++) {
            board.set(x, p1.y() - 1, PREY);
            board.set(x, p2.y() + 1, PREY);
        }
        for (int y = p1.y(); y <= p2.y(); y++) {
            board.set(p1.x() - 1, y, PREY);
            board.set(p2.x() + 1, y, PREY);
        }
    }
}