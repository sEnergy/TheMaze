package themaze.server;

import themaze.Position;
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

        for (int i = 0; i < maze.guards.size(); i++)
            guards.add(new Guard(this, maze.guards.get(i), i));
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
                for (Player p : players)
                    onMove(p);

                for (Player p : players)
                    p.onStart();

                for (Guard g : guards)
                    scheduler.scheduleAtFixedRate(g, 0, speed, TimeUnit.MILLISECONDS);
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
            {
                Position pos = player.getPosition();
                for (Player p : players)
                    p.onChange(pos, (byte) -1);
            }
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

    public void onMove(Guard guard) throws IOException
    {
        synchronized (maze)
        {
            Position position = guard.getPosition();
            byte data = guard.toByte();
            for (Player p : players)
                p.onChange(position, data);
        }
    }

    public void onMove(Player player) throws IOException
    {
        synchronized (maze)
        {
            byte data = player.toByte();
            Position position = player.getPosition();

            for (Player p : players)
                p.onChange(position, (byte) (data + (p == player ? 4 : 0)));

            if (maze.at(position) instanceof Finish)
            {
                winner = player;
                scheduler.shutdownNow();
                for (Player p : players)
                    p.onFinish(p == winner);
            }
        }
    }

    @Override
    public String toString() { return maze.toString(); }
}
