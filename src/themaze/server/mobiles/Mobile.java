package themaze.server.mobiles;

import themaze.server.Game;
import themaze.server.objects.MazeObject;
import themaze.server.types.*;

import java.io.IOException;

public abstract class Mobile
{
    protected Game game;
    protected Position position;
    protected Direction direction;

    public Mobile(Game game, Position start)
    {
        this.game = game;
        position = start;
        direction = Direction.UP;
    }

    public Position getPosition() { return position; }

    public void turnLeft() { turn(-1); }
    public void turnRight() { turn(1); }
    private void turn(int dir)
    {
        int i = direction.ordinal() + dir;
        while (i < 0)
            i = Direction.values().length - 1;
        direction = Direction.values()[i % Direction.values().length];
    }

    public boolean step() throws IOException
    {
        Position newPos = position.add(direction);
        MazeObject obj = game.getObject(newPos);
        if (obj == null || obj.isEnterable())
        {
            position = newPos;
            return true;
        }
        return false;
    }

    public abstract byte toByte();
}
