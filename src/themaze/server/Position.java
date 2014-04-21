package themaze.server;

public class Position
{
    public final byte row, column;
    public Position(byte row, byte column)
    {
        this.row = row;
        this.column = column;
    }

    public Position add(Direction dir)
    {
        switch (dir)
        {
            case Up:
                return new Position((byte) (row - 1), column);
            case Right:
                return new Position(row, (byte) (column + 1));
            case Down:
                return new Position((byte) (row + 1), column);
            case Left:
                return new Position(row, (byte) (column - 1));
        }
        return null;
    }

    public enum Direction
    {
        Up, Right, Down, Left
    }
}
