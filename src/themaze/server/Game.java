package themaze.server;

import themaze.server.mobiles.Mobile;
import themaze.server.mobiles.Player;
import themaze.server.mobiles.Player.Color;
import themaze.server.objects.Finish;
import themaze.server.objects.Gate;
import themaze.server.objects.Key;
import themaze.server.objects.MazeObject;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Game
{
    private final Maze maze;
    private final ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(4);
    private final Map<Mobile, ClientThread> mobiles = new HashMap<>();
    private final List<Color> colors;
    private final int speed;
    private boolean started, finished;

    public Game(Maze maze, int players, int speed) throws IOException
    {
        this.maze = maze;
        this.speed = speed * 500;
        this.colors = new ArrayList<>(Arrays.asList(Color.values()));
        while (colors.size() > players)
            colors.remove(colors.size() - 1);
    }

    public void join(ClientThread thread) throws IOException
    {
        synchronized (maze)
        {
            if (colors.isEmpty())
                return;

            Position start = maze.starts.remove(new Random().nextInt(maze.starts.size()));
            Player player = new Player(this, start, colors.remove(0));
            mobiles.put(player, thread);
            thread.gameJoined(player, maze.rows, maze.columns, maze.toBytes());

            if (colors.isEmpty())
            {
                Server.removeGame(this);
                started = true;
                onMove();
            }
        }
    }

    public void leave(Player player, Color color) throws IOException
    {
        synchronized (maze)
        {
            mobiles.remove(player);
            colors.add(color);
            if (mobiles.isEmpty())
                Server.removeGame(this);
            else if (started && !finished)
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

    public boolean open(boolean hasKeys, Player player, Position position) throws IOException
    {
        synchronized (maze)
        {
            MazeObject obj = maze.at(position);
            boolean res = obj instanceof Gate && ((Gate) obj).open();
            for (Map.Entry<Mobile, ClientThread> entry : mobiles.entrySet())
                entry.getValue().onOpen(hasKeys, entry.getKey() == player, res ? position : null);
            return res;
        }
    }

    public boolean take(Player player, Position position) throws IOException
    {
        synchronized (maze)
        {
            MazeObject obj = maze.at(position);
            boolean res = obj instanceof Key && ((Key) obj).take();
            for (Map.Entry<Mobile, ClientThread> entry : mobiles.entrySet())
                entry.getValue().onTake(entry.getKey() == player, res ? position : null);
            return res;
        }
    }

    public void onFinish(Player player, Position position) throws IOException
    {
        synchronized (maze)
        {
            if (maze.at(position) instanceof Finish)
            {
                finished = true;
                scheduler.shutdownNow();
                for (Map.Entry<Mobile, ClientThread> entry : mobiles.entrySet())
                    entry.getValue().gameFinished(entry.getKey() == player);
            }
        }
    }

    public void onMove() throws IOException
    {
        synchronized (maze)
        {
            for (ClientThread client : mobiles.values())
                client.gameChanged(toBytes(client));
        }
    }

    private byte[] toBytes(ClientThread client)
    {
        synchronized (maze)
        {
            byte[] data = new byte[mobiles.size() * 3];
            int i = 0;
            for (Map.Entry<Mobile, ClientThread> entry : mobiles.entrySet())
            {
                data[i++] = entry.getKey().getPosition().row;
                data[i++] = entry.getKey().getPosition().column;
                data[i++] = (byte) (entry.getKey().toByte() + (entry.getValue() == client ? 4 : 0));
            }
            return data;
        }
    }

    @Override
    public String toString() { return maze.toString(); }
}
