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

    public void turnLeft() throws IOException { turn(-1); }
    public void turnRight() throws IOException { turn(1); }
    private void turn(int dir) throws IOException
    {
        int i = direction.ordinal() + dir;
        while (i < 0)
            i = Direction.values().length - 1;
        direction = Direction.values()[i % Direction.values().length];
        game.onChange();
    }

    public boolean step() throws IOException
    {
        Position pos = position.add(direction);
        MazeObject obj = game.getObject(pos);
        if (obj == null || obj.isEnterable())
        {
            position = pos;
            game.onChange();
            return true;
        }
        return false;
    }

    public abstract byte toByte();
}
