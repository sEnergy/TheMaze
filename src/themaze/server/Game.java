package themaze.server;

import themaze.server.mobiles.Guard;
import themaze.server.mobiles.Mobile;
import themaze.server.mobiles.Player;
import themaze.server.mobiles.Player.Color;
import themaze.server.objects.Finish;
import themaze.server.objects.Gate;
import themaze.server.objects.Key;
import themaze.server.objects.MazeObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Game
{
    private final Maze maze;
    private final ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(4);
    private final List<Player> players = new ArrayList<>();
    private final List<Guard> guards = new ArrayList<>();
    private final List<Color> colors;
    private final int speed;
    private Player winner;
    private boolean started;

    public Game(Maze maze, int players, int speed) throws IOException
    {
        this.maze = maze;
        this.speed = speed * 500;
        this.colors = new ArrayList<>(Arrays.asList(Color.values()));
        while (colors.size() > players)
            colors.remove(colors.size() - 1);

        for (Position pos : maze.guards)
            guards.add(new Guard(this, pos));
    }

    public void join(ClientThread thread) throws IOException
    {
        synchronized (maze)
        {
            if (colors.isEmpty())
                return;

            Position start = maze.starts.remove(new Random().nextInt(maze.starts.size()));
            Player player = new Player(this, thread, start, colors.remove(0));
            players.add(player);
            thread.onJoin(player, maze.rows, maze.columns, maze.toBytes());

            if (colors.isEmpty())
            {
                Server.removeGame(this);
                started = true;
                onMove();

                for (Guard g : guards)
                    scheduler.scheduleAtFixedRate(g, 2, speed, TimeUnit.MILLISECONDS);
            }
        }
    }

    public void leave(Player player, Color color) throws IOException
    {
        synchronized (maze)
        {
            players.remove(player);
            colors.add(color);
            if (players.isEmpty())
                Server.removeGame(this);
            else if (started && winner == null)
                onMove();
        }
    }

    public boolean isEnterable(Position position)
    {
        synchronized (maze)
        {
            MazeObject obj = maze.at(position);
            return obj == null || obj.isEnterable();
        }
    }

    public ScheduledFuture<?> go(Mobile mobile)
    { return scheduler.scheduleAtFixedRate(mobile, 0, speed, TimeUnit.MILLISECONDS); }

    public boolean open(Position position) throws IOException
    {
        synchronized (maze)
        {
            MazeObject obj = maze.at(position);
            boolean res = obj instanceof Gate && ((Gate) obj).open();
            if (res)
                for (Player p : players)
                    p.onChange(position, obj.toByte());
            return res;
        }
    }

    public boolean take(Position position) throws IOException
    {
        synchronized (maze)
        {
            MazeObject obj = maze.at(position);
            boolean res = obj instanceof Key && ((Key) obj).take();
            if (res)
                for (Player p : players)
                    p.onChange(position, obj.toByte());
            return res;
        }
    }

    public void onMove() throws IOException
    {
        synchronized (maze)
        {
            for (Player p : players)
            {
                p.onMove(toBytes(p));
                if (maze.at(p.getPosition()) instanceof Finish)
                    winner = p;
            }

            if (winner != null)
            {
                scheduler.shutdownNow();
                for (Player p : players)
                    p.onFinish(p == winner);
            }
        }
    }

    private byte[] toBytes(Player player)
    {
        synchronized (maze)
        {
            byte[] data = new byte[(players.size() + guards.size()) * 3];
            int i = 0;
            for (Player p : players)
            {
                Position pos = p.getPosition();
                data[i++] = pos.row;
                data[i++] = pos.column;
                data[i++] = (byte) (p.toByte() + (p == player ? 4 : 0));
            }

            for (Guard g : guards)
            {
                Position pos = g.getPosition();
                data[i++] = pos.row;
                data[i++] = pos.column;
                data[i++] = g.toByte();
            }
            return data;
        }
    }

    @Override
    public String toString() { return maze.toString(); }
}
