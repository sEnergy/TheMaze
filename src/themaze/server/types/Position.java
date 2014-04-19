package themaze.server.types;

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
        switch(dir)
        {
            case UP:
                return new Position((byte) (row - 1), column);
            case RIGHT:
                return new Position(row, (byte) (column + 1));
            case DOWN:
                return new Position((byte) (row + 1), column);
            case LEFT:
                return new Position(row, (byte) (column - 1));
        }
        return null;
    }
}
