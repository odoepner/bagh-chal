package net.doepner.baghchal.model;

import net.doepner.baghchal.BoardListener;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static net.doepner.baghchal.model.Piece.INVALID;

/**
 * The game board model
 */
public class Board {

    private static final int[] STEPS = {-1, 0, +1};

    private final int xSize;
    private final int ySize;

    private final Position topLeft;
    private final Position bottomRight;

    private final Piece[][] b;

    private final BoardListener listener;

    public Board(int xSize, int ySize, BoardListener listener) {
        this.xSize = xSize;
        this.ySize = ySize;
        this.listener = listener;
        topLeft = new Position(1, 1);
        bottomRight = new Position(xSize, ySize);
        b = new Piece[xSize + 2][ySize + 2];
    }

    /**
     * Copy constructor that will copy the grid array of the provide board instance.
     * The resulting board will support no BoardListener functionality.
     *
     * @param board An existing board instance
     */
    private Board(Board board) {
        this(board.xSize, board.ySize, BoardListener.NONE);
        for (int x = 0; x < b.length; x++) {
            System.arraycopy(board.b[x], 0, b[x], 0, b[x].length);
        }
    }

    public Board copyBoard() {
        return new Board(this);
    }

    public void processMove(Move move) {
        final Piece piece = get(move.p2());
        if (move.isJump()) {
            clear(move.middle());
            listener.afterJump(piece);
        } else {
            listener.afterStep(piece);
        }
    }

    public Piece movePiece(Move move) {
        final Piece piece = get(move.p1());
        clear(move.p1());
        set(move.p2(), piece);
        return piece;
    }

    public Position pick(Position p, Piece piece) {
        if (get(p) == piece) {
            clear(p);
            listener.afterPicked(piece);
            return p;
        } else {
            return null;
        }
    }

    public void tryStepsWhere(Piece movingPiece, Piece requiredPiece, Consumer<Move> moveProcessor) {
        forAllBoardPositions(p -> {
            if (get(p) == movingPiece) {
                tryDirections(p, requiredPiece, moveProcessor);
            }
        });
    }

    public void forAllPositions(Consumer<Position> positionConsumer) {
        for (int x = 0; x < b.length; x++) {
            for (int y = 0; y < b[x].length; y++) {
                positionConsumer.accept(new Position(x, y));
            }
        }
    }

    private void forAllBoardPositions(Consumer<Position> positionConsumer) {
        for (int x = topLeft.x(); x <= bottomRight.x(); x++) {
            for (int y = topLeft.y(); y <= bottomRight.y(); y++) {
                positionConsumer.accept(new Position(x, y));
            }
        }
    }

    public void tryDirections(Position p, Piece requiredPiece, Consumer<Move> moveProcessor) {
        for (int xStep : STEPS) {
            for (int yStep : STEPS) {
                final Position p1 = p.add(xStep, yStep);
                if (requiredPiece == INVALID || get(p1) == requiredPiece) {
                    processStepAlongLine(p, p1, moveProcessor);
                }
            }
        }
    }

    public void processStepAlongLine(Position p, Position p1, Consumer<Move> moveProcessor) {
        final Move step = new Move(p, p1);
        if (isStepAlongLine(step)) {
            moveProcessor.accept(step);
        }
    }

    public void addPossibleStepsTo(List<Move> moveList, Piece movingPiece) {
        tryStepsWhere(movingPiece, null, moveList::add);
    }

    public void addPossibleJumpsTo(List<Move> moveList, Piece movingPiece, Piece requiredPiece) {
        tryStepsWhere(movingPiece, requiredPiece, step -> addPossibleJump(moveList, step));
    }

    public void addPossibleJump(List<Move> list, Move step1) {
        final Move step2 = step1.repeat();
        if (isStepAlongLine(step2) && isEmpty(step2.p2())) {
            list.add(new Move(step1.p1(), step2.p2()));
        }
    }

    private boolean isStepAlongLine(Move move) {
        return isValidOnBoardPosition(move.p1()) && isValidOnBoardPosition(move.p2()) && move.isStep()
                && (move.p1().hasEvenCoordSum() || move.isOneDimensional());
    }

    private boolean isValidOnBoardPosition(Position pos) {
        return pos.isGreaterOrEqualTo(topLeft) && pos.isLessOrEqualTo(bottomRight);
    }

    public void reset() {
        for (Piece[] pieces : b) {
            Arrays.fill(pieces, null);
        }
        listener.afterReset();
    }

    public Piece get(Position p) {
        return get(p.x(), p.y());
    }

    public void set(Position p, Piece piece) {
        set(p.x(), p.y(), piece);
    }

    private void clear(Position p) {
        set(p, null);
    }

    private boolean isEmpty(Position p) {
        return get(p) == null;
    }

    public Piece get(int x, int y) {
        try {
            return b[x][y];
        } catch (ArrayIndexOutOfBoundsException e) {
            return INVALID;
        }
    }

    public void set(int x, int y, Piece piece) {
        b[x][y] = piece;
    }

    public int getCentreXSize() {
        return xSize;
    }

    public int getCentreYSize() {
        return ySize;
    }

    public int getXSize() {
        return b.length;
    }

    public int getYSize() {
        return b[0].length;
    }

    public boolean isValid(Move move, Piece piece) {
        if (move.isStationary() || !isEmpty(move.p2())) {
            return false;
        }
        // TODO: Factor out the rules of the game (in a flexible way that also work for other games like Alquerque)
        switch (piece) {
            case PREY:
                return isBorderToBoard(move) || isValidOnBoardStep(move);
            case PREDATOR:
                return isStepAlongLine(move) || (move.isJump() && get(move.middle()) == Piece.PREY);
            default:
                return false;
        }
    }

    private boolean isValidOnBoardStep(Move move) {
        return isBorderEmpty() && isStepAlongLine(move);
    }

    private boolean isBorderToBoard(Move move) {
        return isBorderPosition(move.p1()) && !isBorderPosition(move.p2());
    }

    private boolean isBorderEmpty() {
        for (Piece piece : b[topLeft.x() - 1]) {
            if (piece != null) {
                return false;
            }
        }
        for (Piece[] row : b) {
            if (row[topLeft.y() - 1] != null || row[bottomRight.y() + 1] != null) {
                return false;
            }
        }
        for (Piece piece : b[bottomRight.x() + 1]) {
            if (piece != null) {
                return false;
            }
        }
        return true;
    }

    private boolean isBorderPosition(Position p) {
        return p.x() == 0 || p.x() == xSize + 1 || p.y() == 0 || p.y() == ySize + 1;
    }

    public Position getTopLeft() {
        return topLeft;
    }

    public Position getBottomRight() {
        return bottomRight;
    }
}