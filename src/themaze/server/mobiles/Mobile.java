package themaze.server.mobiles;

import themaze.Position;
import themaze.Position.Direction;
import themaze.server.Game;

import java.io.IOException;
import java.util.concurrent.ScheduledFuture;

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
    public boolean isAlive() { return position.row >= 0 && position.column >= 0; }

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

    public boolean step() throws IOException
    {
        synchronized (game)
        {
            if (!isAlive())
                return true;
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
            game.move(this);
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
