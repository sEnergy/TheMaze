package themaze.server.mobiles;

import themaze.server.Game;
import themaze.server.Position;
import themaze.server.Position.Direction;

import java.io.IOException;
import java.util.concurrent.ScheduledFuture;

public abstract class Mobile implements Runnable
{
    private final Object syncRoot = new Object();
    protected Game game;
    protected Position position;
    protected Direction direction;
    private ScheduledFuture<?> task;

    public Mobile(Game game, Position start)
    {
        this.game = game;
        position = start;
        for (Direction dir : Direction.values())
            if (game.isEnterable(position.add(dir)))
            {
                direction = dir;
                break;
            }
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
        game.onMove();
    }

    public boolean step() throws IOException
    {
        Position pos = position.add(direction);
        if (game.isEnterable(pos))
        {
            position = pos;
            game.onMove();
            return true;
        }
        return false;
    }

    public void go()
    {
        synchronized (syncRoot)
        {
            if (task == null)
                task = game.go(this);
        }
    }

    public void stop()
    {
        synchronized (syncRoot)
        {
            if (task != null)
            {
                task.cancel(false);
                task = null;
            }
        }
    }

    @Override
    public void run()
    {
        try
        {
            if (!step())
                stop();
        }
        catch (IOException e) { e.printStackTrace(); }
    }

    public abstract byte toByte();
}
