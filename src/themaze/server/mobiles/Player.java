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

    public boolean isAlive() { return position.isValid(); }
    public byte getKeys() { return keys; }

    public void leave() throws IOException
    {
        synchronized (game)
        {
            stop();
            position = Position.Invalid;
            game.leave(this, color);
        }
    }

    public void die()
    {
        synchronized (game)
        {
            if (isAlive())
            {
                stop();
                position = Position.Invalid;
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

    public void turnLeft() throws IOException { turn(-1); }
    public void turnRight() throws IOException { turn(1); }
    private void turn(int dir) throws IOException
    {
        synchronized (game)
        {
            if (!isAlive())
                return;
            int i = direction.ordinal() + dir;
            while (i < 0)
                i = Direction.values().length - 1;
            direction = Direction.values()[i % Direction.values().length];
            game.move((Mobile) this);
        }
    }

    @Override
    public boolean step() throws IOException
    {
        synchronized (game)
        {
            if (!isAlive())
                return true;
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
