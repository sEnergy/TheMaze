package themaze.server.mobiles;

import themaze.Position;
import themaze.Position.Direction;
import themaze.server.Game;

import java.io.IOException;
import java.util.concurrent.ScheduledFuture;

/**
 * Abstraktní třída pro pohybující se objekty v bludišti.
 * Obsahuje pozici, směr otočení, hru ve které se objekt nachází,
 * počet ušlých kroků a operace pro pohyb.
 *
 * @author Jaroslav Kubík
 * @author Marcel Fiala
 */
public abstract class Mobile implements Runnable
{
    protected final Game game;
    protected Position position;
    protected Direction direction;
    protected int steps;
    private ScheduledFuture<?> task;

    public Mobile(Game game, Position start)
    {
        this.game = game;
        position = start;
    }

    public Position getPosition() { return position; }
    public int getSteps() { return steps; }

    public void go()
    {
        synchronized (game)
        {
            if (task == null)
                task = game.go(this);
        }
    }

    public boolean stop() throws IOException
    {
        synchronized (game)
        {
            if (task != null)
            {
                task.cancel(false);
                task = null;
                return true;
            }
            return false;
        }
    }

    public boolean step() throws IOException
    {
        synchronized (game)
        {
            Position pos = position.add(direction);
            if (game.isEnterable(pos))
            {
                position = pos;
                steps++;
                game.move(this);
                return true;
            }
            return false;
        }
    }

    @Override
    public void run()
    {
        try { step(); }
        catch (IOException e) { e.printStackTrace(); }
    }

    public abstract byte toByte();
}
