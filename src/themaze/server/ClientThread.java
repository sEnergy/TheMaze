package themaze.server;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import themaze.Communication;
import themaze.Communication.Command;
import themaze.server.mobiles.*;
import themaze.server.types.Position;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class ClientThread extends Thread
{
    private final Communication comm;
    private Player player;

    public ClientThread(Socket socket, List<Maze> mazes) throws IOException
    {
        comm = new Communication(socket);
        String[] strs = new String[mazes.size()];
        for (int i = 0; i < mazes.size(); i++)
            strs[i] = mazes.get(i).toString();
        comm.sendStrings(Command.Game, strs);
    }

    @Override
    public void run()
    {
        try
        {
            while (true)
            {
                switch (comm.readCommand())
                {
                    case Game:
                        Server.startGame(this, comm.readInt(), comm.readInt());
                        break;
                    case Join:
                        Server.joinGame(this, comm.readInt());
                        break;
                    case Close:
                        player.leave();
                        player = null;
                        Server.resendGames(this);
                        break;
                    case Keys:
                        comm.sendBytes(Command.Keys, player.getKeys());
                        break;
                    case Take:
                        player.take();
                        break;
                    case Open:
                        player.open();
                        break;
                    case Left:
                        player.turnLeft();
                        break;
                    case Right:
                        player.turnRight();
                        break;
                    case Step:
                        if (!player.step())
                            comm.sendCmd(Command.Step);
                        break;
                    default:
                        throw new NotImplementedException();
                }
            }
        }
        catch (Exception e) { e.printStackTrace(); }
        finally
        {
            Server.removeClient(this);
            if (player != null)
                try { player.leave(); }
                catch (IOException e) { e.printStackTrace(); }
        }
    }

    public void gamesChanged(List<Game> games) throws IOException
    {
        if (player == null)
        {
            String[] strs = new String[games.size()];
            for (int i = 0; i < games.size(); i++)
                strs[i] = games.get(i).toString();
            comm.sendStrings(Command.Join, strs);
        }
    }

    public void gameJoined(Player player, byte rows, byte columns, byte[] data) throws IOException
    {
        this.player = player;
        comm.sendMaze(rows, columns, data);
    }

    public void gameFinished(boolean winner) throws IOException
    { comm.sendBytes(Command.Close, winner ? 1 : 0); }

    public void gameChanged(byte[] data) throws IOException
    { comm.sendMaze((byte) 1, (byte) data.length, data); }

    public void onTake(boolean owner, Position position) throws IOException
    {
        if (position != null)
            comm.sendBytes(Command.Take, owner ? 1 : 0, position.row, position.column);
        else if (owner)
            comm.sendBytes(Command.Take, 2);
    }

    public void onOpen(boolean hadKeys, boolean owner, Position position) throws IOException
    {
        if (position != null)
            comm.sendBytes(Command.Open, owner ? 1 : 0, position.row, position.column);
        else if (owner)
            comm.sendBytes(Command.Open, hadKeys ? 3 : 2);
    }
}
