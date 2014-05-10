package themaze.server;

import themaze.Position;
import themaze.server.mobiles.*;
import themaze.server.mobiles.Player.Color;
import themaze.server.objects.*;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.*;

public class Game
{
    private final Maze maze;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final Map<Player, ClientThread> players = new HashMap<>();
    private final List<Guard> guards = new ArrayList<>();
    private final List<Color> colors;
    private final int speed;
    private Player winner;
    private boolean started;

    public Game(Maze maze, int players, int speed)
    {
        this.maze = maze;
        this.speed = speed * 500;
        this.colors = new ArrayList<>(Arrays.asList(Color.values()));
        while (colors.size() > players)
            colors.remove(colors.size() - 1);

        for (int i = 0; i < maze.guards.size(); i++)
            guards.add(new Guard(this, maze.guards.get(i), i));
    }

    public synchronized void join(ClientThread thread) throws IOException
    {
        if (colors.isEmpty())
            return;

        Position start = maze.starts.remove(new Random().nextInt(maze.starts.size()));
        Color color = colors.remove(0);
        Player player = new Player(this, start, color);
        players.put(player, thread);
        thread.onJoin(player, (byte) (color.ordinal() + 1), maze.rows, maze.columns, maze.toBytes());

        if (colors.isEmpty())
        {
            Server.removeGame(this);
            started = true;
            for (Guard g : guards)
                move(g);

            for (Entry<Player, ClientThread> p : players.entrySet())
            {
                move((Mobile) p.getKey());
                p.getValue().onStart();
            }

            for (Guard g : guards)
                scheduler.scheduleAtFixedRate(g, 0, speed, TimeUnit.MILLISECONDS);
        }
    }

    public synchronized void leave(Player player, Color color) throws IOException
    {
        players.remove(player);
        colors.add(color);
        maze.starts.add(player.getPosition());
        if (players.isEmpty())
        {
            scheduler.shutdownNow();
            Server.removeGame(this);
        }
        else if (started && winner == null)
        {
            for (Entry<Player, ClientThread> p : players.entrySet())
            {
                p.getValue().onChange(player.getPosition(), player.toByte());
                if (p.getKey().isAlive())
                    return;
            }
            end();
        }
    }

    public synchronized boolean isEnterable(Position position)
    {
        MazeObject obj = maze.at(position);
        return obj == null || obj.isEnterable();
    }

    public ScheduledFuture<?> go(Mobile mobile)
    { return scheduler.scheduleAtFixedRate(mobile, 0, speed, TimeUnit.MILLISECONDS); }

    public synchronized void stop(Player player) throws IOException
    { players.get(player).onStop(); }

    public synchronized boolean open(Position position) throws IOException
    {
        MazeObject obj = maze.at(position);
        if (obj instanceof Gate && ((Gate) obj).open())
        {
            for (ClientThread t : players.values())
                t.onChange(position, obj.toByte());
            return true;
        }
        return false;
    }

    public synchronized boolean take(Position position) throws IOException
    {
        MazeObject obj = maze.at(position);
        if (obj instanceof Key && ((Key) obj).take())
        {
            for (ClientThread t : players.values())
                t.onChange(position, obj.toByte());
            return true;
        }
        return false;
    }

    public synchronized void move(Mobile mobile) throws IOException
    {
        byte data = mobile.toByte();
        Position pos = mobile.getPosition();

        for (ClientThread t : players.values())
            t.onChange(pos, data);

        for (Player player : players.keySet())
            if (player != mobile && player.getPosition().equals(pos))
                kill(player);
    }

    public synchronized void move(Player player) throws IOException
    {
        for (Guard guard : guards)
            if (guard.getPosition().equals(player.getPosition()))
                kill(player);

        if (maze.at(player.getPosition()) instanceof Finish)
        {
            winner = player;
            end();
        }
    }

    private void kill(Player player) throws IOException
    {
        player.die();
        for (ClientThread t : players.values())
            t.onInfo(-player.toByte(), player.getSteps());

        for (Player p : players.keySet())
            if (p.isAlive())
                return;
        end();
    }

    private void end() throws IOException
    {
        scheduler.shutdownNow();
        for (ClientThread t : players.values())
        {
            for (Player p : players.keySet())
                if (p.isAlive())
                    t.onInfo(p.toByte(), p.getSteps());
            t.onFinish(winner == null ? -1 : winner.toByte());
        }
    }

    @Override
    public String toString() { return maze.toString(); }
}
