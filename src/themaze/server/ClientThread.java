package themaze.server;

import themaze.server.mobiles.*;
import themaze.server.net.Command;
import themaze.server.net.Communication;

import java.io.*;
import java.net.Socket;

import static themaze.server.net.Command.CommandType;

public class ClientThread extends Thread
{
    private Game game;
    private Player player;
    private final Communication comm;

    public ClientThread(Socket socket) throws IOException
    {
        this.comm = new Communication(socket);
    }

    @Override
    public void run()
    {
        try
        {
            while (true)
            {
                String response = handleCommand(comm.receive());
                if (response == null)
                {
                    comm.close();
                    break;
                }
            }
        }
        catch (IOException e) { System.out.print(e.getMessage()); }
    }

    public void gameStarted(Mobile player, byte[] data) throws IOException
    {
        synchronized (comm) { this.player = (Player)player; }
        comm.sendData(2, data);
    }

    public void gameFinished(boolean winner) throws IOException
    {
        comm.sendString(3, winner ? "You won!" : "You lost.");
    }

    public void gameJoined(int x, int y) throws IOException
    {
        comm.sendBytes(1, x, y);
    }

    private String handleCommand(Command cmd)
    {
        try
        {
            if (game == null && cmd.type.ordinal() > CommandType.Show.ordinal())
                return "You have to start or join a game first!";
            if (game != null && player == null)
                return "Game isn't ready yet!";
            switch (cmd.type)
            {
                case Game:
                    if (game != null)
                        return "You are already in a game!";
                    game = Game.startGame(this, cmd.getData(), 1);
                    return "Game created.";
                case Join:
                    game = Game.joinGame(this);
                    if (game == null)
                        return "No game to join.";
                    return "Game joined.";
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
}
