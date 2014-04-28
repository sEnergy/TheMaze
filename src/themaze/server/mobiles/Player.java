package themaze.server.mobiles;

import themaze.Communication.Command;
import themaze.Position;
import themaze.Position.Direction;
import themaze.server.ClientThread;
import themaze.server.Game;

import java.io.IOException;

public class Player extends Mobile
{
    private final ClientThread thread;
    private final Color color;
    private Direction direction;
    private byte keys;
    private int steps;

    public Player(Game game, ClientThread thread, Position start, Color color)
    {
        super(game, start);
        this.thread = thread;
        this.color = color;
        for (Position.Direction dir : Position.Direction.values())
            if (game.isEnterable(position.add(dir)))
            {
                direction = dir;
                break;
            }
    }

    public boolean isAlive() { return position.row >= 0 && position.column >= 0; }
    public byte getKeys() { return keys; }
    public void turnLeft() throws IOException { turn(-1); }
    public void turnRight() throws IOException { turn(1); }
    private void turn(int dir) throws IOException
    {
        synchronized (game)
        {
            int i = direction.ordinal() + dir;
            while (i < 0)
                i = Position.Direction.values().length - 1;
            direction = Position.Direction.values()[i % Position.Direction.values().length];
            game.move(this);
        }
    }

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
            stop();
            position = new Position(-1, -1);
        }
    }

    public void take() throws IOException
    {
        synchronized (game)
        {
            if (game.take(position.add(direction)))
            {
                keys++;
                thread.sendCmd(Command.Take, 0);
            }
            else
                thread.sendCmd(Command.Take, 1);
        }
    }

    public void open() throws IOException
    {
        synchronized (game)
        {
            if (keys <= 0)
                thread.sendCmd(Command.Open, 1);
            else if (!game.open(position.add(direction)))
                thread.sendCmd(Command.Open, 2);
            else
            {
                keys--;
                thread.sendCmd(Command.Open, 0);
            }
        }
    }

    @Override
    public void step() throws IOException
    {
        synchronized (game)
        {
            Position pos = position.add(direction);
            if (game.isEnterable(pos))
            {
                position = pos;
                steps++;
                game.move(this);
            }
            else
            {
                thread.sendCmd(Command.Step);
                stop();
            }
        }
    }

    @Override
    public byte toByte()
    { return (byte) (10 + 10 * color.ordinal() + direction.ordinal()); }

    public void onChange(Position position, byte newByte) throws IOException
    { thread.onChange(position, newByte); }

    public void onFinish(boolean winner) throws IOException
    { thread.onFinish(winner); }

    public void onStart() throws IOException
    { thread.onStart(); }

    public void onInfo() throws IOException
    { thread.onInfo(toByte(), steps); }

    public enum Color
    {
        Red, Green, Blue, TheFourth
    }
}
