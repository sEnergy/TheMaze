package themaze.server;

import java.io.*;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class Server
{
    private static final List<Maze> mazes = new ArrayList<>();
    private static final List<Game> games = new ArrayList<>();
    private static final List<ClientThread> clients = new ArrayList<>();

    public static void main(String[] args)
    {
        int port;
        try
        {
            port = Integer.parseInt(args[0]);
            if (port < 0 || port > 0xFFFF)
                throw new NumberFormatException();

            for (File file : getMazeFiles())
                mazes.add(new Maze(file));

            ServerSocket server = new ServerSocket(port);
            while (true)
            {
                ClientThread client = new ClientThread(server.accept(), mazes);
                synchronized (games)
                {
                    clients.add(client);
                    client.gamesChanged(games);
                }
                client.start();
            }
        }
        catch (NumberFormatException e) { System.err.println("Invalid port."); }
        catch (IOException e) { System.err.println(e.getMessage()); }
    }

    private static File[] getMazeFiles()
    {
        return new File("examples").listFiles(new FilenameFilter()
        {
            public boolean accept(File dir, String name)
            { return name.endsWith(".maz"); }
        });
    }

    public static void removeClient(ClientThread thread)
    { synchronized (games) { clients.remove(thread); } }

    public static void resendGames(ClientThread thread) throws IOException
    { synchronized (games) { thread.gamesChanged(games); } }

    public static void startGame(ClientThread thread, int id, int players, int speed) throws IOException
    {
        synchronized (games)
        {
            Game game = new Game(new Maze(mazes.get(id)), players, speed);
            games.add(game);
            game.join(thread);
            for (ClientThread client : clients)
                client.gamesChanged(games);
        }
    }

    public static void joinGame(ClientThread thread, int id) throws IOException
    {
        synchronized (games)
        {
            Game game = games.get(id);
            game.join(thread);
            for (ClientThread client : clients)
                client.gamesChanged(games);
        }
    }

    public static void removeGame(Game game) throws IOException
    {
        synchronized (games)
        {
            games.remove(game);
            for (ClientThread client : clients)
                client.gamesChanged(games);
        }
    }
}
