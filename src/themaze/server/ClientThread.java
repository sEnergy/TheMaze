package themaze.server;

import themaze.Communication;
import themaze.Communication.Command;
import themaze.Position;
import themaze.server.mobiles.Player;

import java.io.IOException;
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
                handleCmd(comm.readCommand());
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

    public void onJoin(Player player, byte color, byte rows, byte columns, byte[] data) throws IOException
    {
        this.player = player;
        comm.sendData(Command.Maze, data, color, rows, columns);
    }

    public void onStart() throws IOException
    { comm.sendCmd(Command.Close, 0); }

    public void onFinish(boolean winner) throws IOException
    { comm.sendCmd(Command.Close, winner ? 1 : 2); }

    public void onChange(Position position, byte newByte) throws IOException
    { comm.sendCmd(Command.Change, position.row, position.column, newByte); }

    public void sendCmd(Command cmd, int... data) throws IOException
    { comm.sendCmd(cmd, data); }

    private void handleCmd(Command cmd) throws IOException, IllegalAccessException, InstantiationException
    {
        switch (cmd)
        {
            case Game:
                Server.startGame(this, comm.readByte(), comm.readByte(), comm.readByte());
                break;
            case Join:
                Server.joinGame(this, comm.readByte());
                break;
            case Close:
                player.leave();
                player = null;
                Server.resendGames(this);
                break;
            case Keys:
                comm.sendCmd(Command.Keys, player.getKeys());
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
                player.step();
                break;
            case Go:
                player.go();
                break;
            case Stop:
                player.stop();
                break;
        }
    }
}
