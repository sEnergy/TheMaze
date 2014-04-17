package themaze.server;

import themaze.server.mobiles.*;
import themaze.server.objects.*;
import themaze.server.types.Position;

import java.io.*;
import java.util.*;

public class Game
{
    private static final List<Game> games = new ArrayList<>();
    public static Game startGame(ClientThread thread, String name, int players) throws IOException
    {
        synchronized (games)
        {
            Game game = new Game(name, players);
            games.add(game);
            game.join(thread);
            return game;
        }
    }

    public static Game joinGame(ClientThread thread) throws IOException
    {
        synchronized (games)
        {
            Game game = games.get(0);
            game.join(thread);
            return game;
        }
    }

    private final MazeObject[][] maze;
    private final List<Position> starts = new ArrayList<>();
    private final Map<Mobile, ClientThread> mobiles = new HashMap<>();
    private int players;

    private Game(String name, int players) throws IOException
    {
        this.players = players;
        try (BufferedReader br = new BufferedReader(new FileReader("examples/" + name + ".maz")))
        {
            String[] data;
            String line = br.readLine();
            if (line == null || (data = line.split("\\s+")).length != 2)
                throw new IOException("Invalid maze format");

            int rows = Integer.parseInt(data[0]);
            int columns = Integer.parseInt(data[1]);
            maze = new MazeObject[rows][columns];

            for (int x = 0; x < rows; x++)
            {
                line = br.readLine();
                if (line == null || (data = line.split("\\s+")).length != columns)
                    throw new IOException("Invalid maze format");

                for (int y = 0; y < columns; y++)
                {
                    if (data[y].equals("S"))
                        starts.add(new Position(x, y));
                    else
                        maze[x][y] = parseObject(data[y]);
                }
            }
        }
    }

    private themaze.server.objects.MazeObject parseObject(String type) throws IllegalArgumentException
    {
        switch (type)
        {
            case "G":
                return new Gate();
            case "W":
                return new Wall();
            case "K":
                return new Key();
            case "F":
                return new Finish();
            case "-":
                return null;
        }
        throw new IllegalArgumentException("Invalid maze object");
    }

    private void join(ClientThread thread) throws IOException
    {
        synchronized (maze)
        {
            if (players > 0)
            {
                players--;
                Position start = starts.get(new Random().nextInt(starts.size()));
                starts.remove(start);
                mobiles.put(new Player(this, start), thread);
                thread.gameJoined(maze.length, maze[0].length);

                if (players < 1)
                    for (Map.Entry<Mobile, ClientThread> entry : mobiles.entrySet())
                    {
                        entry.getValue().gameStarted(entry.getKey());
                        entry.getValue().gameChanged(toBytes());
                    }

            }
        }
    }

    public void leave(Player player)
    {
        synchronized (maze)
        {
            mobiles.remove(player);
            if (mobiles.isEmpty())
                synchronized (games) { games.remove(this); }
        }
    }

    public MazeObject getObject(Position position) { return maze[position.x][position.y]; }

    public void onFinish(Player player) throws IOException
    {
        synchronized (maze)
        {
            for (Map.Entry<Mobile, ClientThread> entry : mobiles.entrySet())
                entry.getValue().gameFinished(entry.getKey() == player);

            synchronized (games) { games.remove(this); }
        }
    }

    public void onChange() throws IOException
    {
        synchronized (maze)
        {
            for (ClientThread client : mobiles.values())
                client.gameChanged(toBytes());

            synchronized (games) { games.remove(this); }
        }
    }


    private byte[] toBytes()
    {
        byte[] data = new byte[maze.length * maze[0].length];
        for (int x = 0; x < maze.length; x++)
            for (int y = 0; y < maze[x].length; y++)
            {
                byte b = 0;
                Mobile m = getMobile(x, y);
                if (m != null)
                    b = m.toByte();
                else if (maze[x][y] != null)
                    b = maze[x][y].toByte();
                data[y + x * maze[x].length] = b;
            }
        return data;
    }

    private Mobile getMobile(int x, int y)
    {
        for (Mobile m : mobiles.keySet())
            if (m.getPosition().x == x && m.getPosition().y == y)
                return m;
        return null;
    }
}
