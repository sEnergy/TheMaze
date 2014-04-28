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
    private final ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(1);
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
            Color color = colors.remove(0);
            Player player = new Player(this, thread, start, color);
            players.add(player);
            thread.onJoin(player, (byte) (color.ordinal() + 1), maze.rows, maze.columns, maze.toBytes());

            if (colors.isEmpty())
            {
                Server.removeGame(this);
                started = true;
                for (Player p : players)
                    move(p);

                for (Guard g : guards)
                    move(g);

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
            {
                scheduler.shutdownNow();
                Server.removeGame(this);
            }
            else if (started && winner == null)
                for (Player p : players)
                    p.onChange(player.getPosition(), player.toByte());
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

    private void kill(Player player) throws IOException
    {
        synchronized (maze)
        {
            player.die();
            for (Player p : players)
                p.onChange(player.getPosition(), player.toByte());

            for (Player p : players)
                if (p.isActive())
                    return;

            scheduler.shutdownNow();
        }
    }

    public void move(Guard guard) throws IOException
    {
        synchronized (maze)
        {
            byte data = guard.toByte();
            Position pos = guard.getPosition();

            for (Player p : players)
                p.onChange(pos, data);

            for (Player player : players)
                if (player.getPosition().equals(pos))
                    kill(player);
        }
    }

    public void move(Player player) throws IOException
    {
        synchronized (maze)
        {
            byte data = player.toByte();
            Position pos = player.getPosition();

            for (Player p : players)
                p.onChange(pos, data);

            for (Guard guard : guards)
                if (guard.getPosition().equals(pos))
                    kill(player);

            for (Player p : players)
                if (p != player && p.getPosition().equals(pos))
                    kill(p);

            if (maze.at(player.getPosition()) instanceof Finish)
            {
                winner = player;
                scheduler.shutdownNow();
                for (Player p : players)
                    if (p.isActive())
                        p.onFinish(p == winner);
            }
        }
    }

    @Override
    public String toString() { return maze.toString(); }
}
