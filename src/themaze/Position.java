package themaze;

public class Position
{
    public static final Position Invalid = new Position(-1, -1);
    public final byte row, column;
    public Position(int row, int column)
    {
        this.row = (byte) row;
        this.column = (byte) column;
    }

    public Position add(Direction dir)
    {
        switch (dir)
        {
            case Up:
                return new Position(row - 1, column);
            case Right:
                return new Position(row, column + 1);
            case Down:
                return new Position(row + 1, column);
            case Left:
                return new Position(row, column - 1);
        }
        return null;
    }

    public boolean isValid() { return row >= 0 && column >= 0; }

    @Override
    public int hashCode() { return row * column; }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof Position)
        {
            Position pos = (Position) obj;
            return pos.row == row && pos.column == column;
        }
        return false;
    }

    public enum Direction
    {
        Up, Right, Down, Left
    }
}
