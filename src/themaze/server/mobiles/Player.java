package themaze.server.mobiles;

import themaze.Position;
import themaze.Position.Direction;
import themaze.server.Game;

import java.io.IOException;

public class Player extends Mobile
{
    private final Color color;
    private byte keys;

    public Player(Game game, Position start, Color color)
    {
        super(game, start);
        this.color = color;
        for (Direction dir : Direction.values())
            if (game.isEnterable(position.add(dir)))
            {
                direction = dir;
                break;
            }
    }

    public byte getKeys() { return keys; }

    public void leave() throws IOException
    {
        synchronized (game)
        {
            stop();
            position = new Position(-1, 0);
            game.leave(this, color);
        }
    }

    public void die() throws IOException
    {
        synchronized (game)
        {
            if (isAlive())
            {
                stop();
                position = new Position(0, -1);
            }
        }
    }

    public byte take() throws IOException
    {
        synchronized (game)
        {
            if (!isAlive())
                return -1;
            if (game.take(position.add(direction)))
            {
                keys++;
                return 0;
            }
            else
                return 1;
        }
    }

    public byte open() throws IOException
    {
        synchronized (game)
        {
            if (!isAlive())
                return -1;
            if (keys <= 0)
                return 1;
            else if (!game.open(position.add(direction)))
                return 2;
            else
            {
                keys--;
                return 0;
            }
        }
    }

    @Override
    public boolean step() throws IOException
    {
        synchronized (game)
        {
            if (!isAlive())
                return false;
            if (super.step())
            {
                game.move(this);
                return true;
            }
            stop();
            return false;
        }
    }

    @Override
    public byte toByte()
    { return (byte) (10 + 10 * color.ordinal() + direction.ordinal()); }

    public enum Color
    {
        Red, Green, Blue, TheFourth
    }
}
