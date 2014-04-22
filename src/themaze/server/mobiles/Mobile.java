package themaze.server.mobiles;

import themaze.server.Game;
import themaze.Position;

import java.io.IOException;
import java.util.concurrent.ScheduledFuture;

public abstract class Mobile implements Runnable
{
    protected final Game game;
    protected Position position;
    private ScheduledFuture<?> task;

    public Mobile(Game game, Position start)
    {
        this.game = game;
        position = start;
    }

    public Position getPosition() { return position; }

    public void go()
    {
        synchronized (game)
        {
            if (task == null)
                task = game.go(this);
        }
    }

    public void stop()
    {
        synchronized (game)
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
        try { step(); }
        catch (IOException e) { e.printStackTrace(); }
    }

    public abstract byte toByte();
    public abstract void step() throws IOException;
}
