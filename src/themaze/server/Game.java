package themaze.server;

import themaze.server.mobiles.*;
import themaze.server.objects.*;
import themaze.server.types.Position;

import java.io.*;
import java.util.*;

public class Game
{
    private final Maze maze;
    private final Map<Mobile, ClientThread> mobiles = new HashMap<>();
    private int players;
    private boolean started, finished;

    public Game(Maze maze, int players) throws IOException
    {
        this.maze = maze;
        this.players = players;
    }

    public void join(ClientThread thread) throws IOException
    {
        synchronized (maze)
        {
            if (players > 0)
            {
                players--;
                Position start = maze.starts.get(new Random().nextInt(maze.starts.size()));
                maze.starts.remove(start);
                Player player = new Player(this, start);
                mobiles.put(player, thread);
                thread.gameJoined(player, maze.rows, maze.columns, maze.toBytes());

                if (players < 1)
                {
                    Server.removeGame(this);
                    started = true;
                    onChange();
                }
            }
        }
    }

    public void leave(Player player) throws IOException
    {
        synchronized (maze)
        {
            mobiles.remove(player);
            players++;
            if (mobiles.isEmpty())
                Server.removeGame(this);
            else if (started && !finished)
                onChange();
        }
    }

    public MazeObject getObject(Position position) { return maze.at(position); }

    public void onFinish(Player player) throws IOException
    {
        synchronized (maze)
        {
            finished = true;
            for (Map.Entry<Mobile, ClientThread> entry : mobiles.entrySet())
                entry.getValue().gameFinished(entry.getKey() == player);
        }
    }

    public void onChange() throws IOException
    {
        synchronized (maze)
        {
            for (ClientThread client : mobiles.values())
                client.gameChanged(toBytes());
        }
    }

    public void onOpen(boolean hadKeys, Player player, Position pos) throws IOException
    {
        synchronized (maze)
        {
            for (Map.Entry<Mobile, ClientThread> entry : mobiles.entrySet())
                entry.getValue().onOpen(hadKeys, entry.getKey() == player, pos);
        }
    }

    public void onTake(Player player, Position pos) throws IOException
    {
        synchronized (maze)
        {
            for (Map.Entry<Mobile, ClientThread> entry : mobiles.entrySet())
                entry.getValue().onTake(entry.getKey() == player, pos);
        }
    }

    private byte[] toBytes()
    {
        byte[] data = new byte[mobiles.size() * 3];
        int i = 0;
        for (Mobile mobile : mobiles.keySet())
        {
            data[i++] = mobile.getPosition().row;
            data[i++] = mobile.getPosition().column;
            data[i++] = mobile.toByte();
        }
        return data;
    }

    @Override
    public String toString() { return maze.toString(); }
}
