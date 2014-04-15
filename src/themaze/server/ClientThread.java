package themaze.server;

import themaze.server.mobiles.*;

import java.io.*;
import java.net.Socket;

public class ClientThread extends Thread
{
    private final Socket socket;
    private Game game;
    private Player player;
    private PrintWriter writer;

    public ClientThread(Socket socket) { this.socket = socket; }

    @Override
    public void run()
    {
        try
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
            String line;
            while ((line = reader.readLine()) != null)
            {
                String response = handleCommand(line);
                if (response == null)
                {
                    socket.close();
                    break;
                }
                writer.println(response);
            }
        }
        catch (IOException e) { e.printStackTrace(); }
    }

    public void gameStarted(Mobile player)
    {
        synchronized (socket)
        {
            this.player = (Player)player;
            writer.println("Game started!");
        }
    }

    public void gameFinished(boolean winner)
    {
        synchronized (socket)
        {
            writer.println(winner ? "You won!" : "You lost.");
        }
    }

    private String handleCommand(String str)
    {
        try
        {
            Command cmd = parseCommand(str);
            if (cmd == Command.Invalid)
                return "Invalid command";
            if (game == null && cmd != Command.Game && cmd != Command.Close && cmd != Command.Join)
                return "You have to start or join a game first!";
            if (game != null && player == null)
                return "Game isn't ready yet!";
            switch (cmd)
            {
                case Game:
                    if (game != null)
                        return "You are already in a game!";
                    game = Game.startGame(this, str.substring(4).trim(), 2);
                    return "Game created.";
                case Join:
                    game = Game.joinGame(this);
                    if (game == null)
                        return "No game to join.";
                    return "Game joined.";
                case Show:
                    return game.toString();
                case Close:
                    game.leave(player);
                    return null;
                case Left:
                    player.turnLeft();
                    return "You have turned left.";
                case Right:
                    player.turnRight();
                    return "You have turned right.";
                case Step:
                    if (player.step())
                        return "You made step forward!";
                    return "You can't make a step that way.";
                case Keys:
                    return String.format("You have %d key(s)\n", player.getKeys());
                case Take:
                    if (player.take())
                        return "You picked up a key.";
                    return "There is no key in front of you.";
                case Open:
                    if (player.open())
                        return "You opened the gate.";
                    return "Cant't open gate. (no gate or no key)";
            }
        }
        catch (Exception ex) { return ex.getMessage(); }
        return "Invalid command";
    }

    private Command parseCommand(String str)
    {
        switch (str.toLowerCase())
        {
            case "show":
                return Command.Show;
            case "join":
                return Command.Join;
            case "close":
                return Command.Close;
            case "left":
                return Command.Left;
            case "right":
                return Command.Right;
            case "step":
                return Command.Step;
            case "keys":
                return Command.Keys;
            case "take":
                return Command.Take;
            case "open":
                return Command.Open;
        }
        if (str.toLowerCase().startsWith("game "))
            return Command.Game;
        return Command.Invalid;
    }

    private enum Command { Game, Join, Show, Close, Left, Right, Step, Keys, Take, Open, Invalid }
}
