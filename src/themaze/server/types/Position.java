package themaze.server.types;

public class Position
{
    public final int x, y;

    public Position(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public Position add(Direction dir)
    {
        switch(dir)
        {
            case UP:
                return new Position(x - 1, y);
            case RIGHT:
                return new Position(x, y + 1);
            case DOWN:
                return new Position(x + 1, y);
            case LEFT:
                return new Position(x, y - 1);
        }
        return null;
    }
}
